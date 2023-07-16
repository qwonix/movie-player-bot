package ru.qwonix.telegram.movieplayerbot.telegram.bot.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.qwonix.telegram.movieplayerbot.entity.Episode;
import ru.qwonix.telegram.movieplayerbot.entity.MessageIds;
import ru.qwonix.telegram.movieplayerbot.entity.Season;
import ru.qwonix.telegram.movieplayerbot.entity.User;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchEpisodeException;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchSeasonException;
import ru.qwonix.telegram.movieplayerbot.service.episode.EpisodeService;
import ru.qwonix.telegram.movieplayerbot.service.season.SeasonService;
import ru.qwonix.telegram.movieplayerbot.service.telegram.BotService;
import ru.qwonix.telegram.movieplayerbot.service.user.UserService;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.EpisodeCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.SeasonCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.TelegramConfig;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.utils.TelegramBotUtils;


import java.util.*;

@Component
@Slf4j
public class SeasonCallbackHandler  {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    private final TelegramConfig telegramConfig;

    private final BotService botService;

    private final EpisodeService episodeService;

    private final SeasonService seasonService;

    public SeasonCallbackHandler(ObjectMapper objectMapper, UserService userService, TelegramConfig telegramConfig, BotService botService, EpisodeService episodeService, SeasonService seasonService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.telegramConfig = telegramConfig;
        this.botService = botService;
        this.episodeService = episodeService;
        this.seasonService = seasonService;
    }

    public void handle(User user, SeasonCallbackData callbackData) throws NoSuchSeasonException, NoSuchEpisodeException, JsonProcessingException {
        Optional<Season> optionalSeason = seasonService.findById(callbackData.seasonId);
        Season season;
        if (optionalSeason.isPresent()) {
            season = optionalSeason.get();
        } else {
            throw new NoSuchSeasonException("Такого сезона не существует. Попробуйте найти его заново.");
        }


        int totalEpisodesCountInSeason = episodeService.countAllBySeason(season);
        int keyboardPageEpisodesLimit = telegramConfig.KEYBOARD_PAGE_EPISODES_MAX;
        int pagesCount = (int) Math.ceil(totalEpisodesCountInSeason / (double) keyboardPageEpisodesLimit);

        List<Episode> seasonEpisodes
                = episodeService.findAllBySeasonOrderByNumberWithLimitAndPage(season, keyboardPageEpisodesLimit, callbackData.page);

        InlineKeyboardMarkup keyboard;
        if (seasonEpisodes.isEmpty()) {
            throw new NoSuchEpisodeException("В сезоне отсутствуют серии");
        } else {
            keyboard = createControlButtons(season.getId(), seasonEpisodes, callbackData.page, pagesCount);
        }

        String text = createText(season, totalEpisodesCountInSeason);

        MessageIds messagesIds = user.getMessageIds();
        if (messagesIds.hasEpisodeMessageId()) {
            botService.deleteMessage(user, messagesIds.getEpisodeMessageId());
            messagesIds.setEpisodeMessageId(null);
        }
        if (messagesIds.hasVideoMessageId()) {
            botService.deleteMessage(user, messagesIds.getVideoMessageId());
            messagesIds.setVideoMessageId(null);
        }

        if (messagesIds.hasSeasonMessageId()) {
            botService.editPhotoWithMarkdownTextAndKeyboard(user
                    , messagesIds.getSeasonMessageId()
                    , text
                    , season.getPreviewTgFileId()
                    , keyboard);

        } else {
            Integer seriesMessageId = botService.sendPhotoWithMarkdownTextAndKeyboard(user
                    , text
                    , season.getPreviewTgFileId()
                    , keyboard);

            messagesIds.setSeasonMessageId(seriesMessageId);
        }

        userService.merge(user);
    }

    private static String createText(Season season, int episodesCount) {
        return String.format("*%s –* `%s сезон`\n", season.getSeries().getTitle(), season.getNumber())
                + '\n'
                + String.format("_%s_\n", season.getDescription())
                + '\n'
                + String.format("*Количество эпизодов*: `%d` / *%s*\n", episodesCount, season.getTotalEpisodesCount())
//                + String.format("Премьера: `%s`\n", season.getFormattedPremiereReleaseDate())
//                + String.format("Финал: `%s`\n", season.getFormattedFinalReleaseDate())
                ;
    }

    private InlineKeyboardMarkup createControlButtons(Long seasonId, List<Episode> episodes, int page, int pagesCount) throws JsonProcessingException {
        Map<String, String> keyboardMap = new LinkedHashMap<>();
        for (Episode episode : episodes) {
            String episodeCallbackJson = objectMapper.writeValueAsString(new EpisodeCallbackData(episode.getId()));
            keyboardMap.put(episode.getSeason().getNumber() + "×" + episode.getNumber() + " «" + episode.getTitle() + "»", episodeCallbackJson);
        }

        List<List<InlineKeyboardButton>> inlineKeyboard = TelegramBotUtils.createKeyboard(keyboardMap, 5, 2);

        if (pagesCount > 1) {
            InlineKeyboardButton previous;
            InlineKeyboardButton next;

            if (page == 0) {
                previous = InlineKeyboardButton.builder()
                        .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                        .text("×").build();
            } else {
                previous = InlineKeyboardButton.builder()
                        .callbackData(objectMapper.writeValueAsString(new SeasonCallbackData(seasonId, page - 1)))
                        .text("‹").build();
            }

            if (pagesCount == page + 1) {
                next = InlineKeyboardButton.builder()
                        .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                        .text("×").build();
            } else {
                next = InlineKeyboardButton.builder()
                        .callbackData(objectMapper.writeValueAsString(new SeasonCallbackData(seasonId, page + 1)))
                        .text("›").build();
            }

            InlineKeyboardButton current = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                    .text(page + 1 + "/" + pagesCount).build();


            List<InlineKeyboardButton> controlButtons = Arrays.asList(previous, current, next);
            inlineKeyboard.add(controlButtons);
        }

        return new InlineKeyboardMarkup(inlineKeyboard);
    }
}
