package ru.qwonix.telegram.movieplayerbot.telegram.bot.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.qwonix.telegram.movieplayerbot.entity.MessageIds;
import ru.qwonix.telegram.movieplayerbot.entity.Season;
import ru.qwonix.telegram.movieplayerbot.entity.Series;
import ru.qwonix.telegram.movieplayerbot.entity.User;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchEpisodeException;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchSeriesException;
import ru.qwonix.telegram.movieplayerbot.service.season.SeasonService;
import ru.qwonix.telegram.movieplayerbot.service.series.SeriesService;
import ru.qwonix.telegram.movieplayerbot.service.telegram.BotService;
import ru.qwonix.telegram.movieplayerbot.service.user.UserService;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.SeasonCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.SeriesCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.TelegramConfig;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.utils.TelegramBotUtils;

import java.util.*;

@Component
@Slf4j
public class SeriesCallbackHandler {
    private final ObjectMapper objectMapper;
    private final BotService botService;
    private final TelegramConfig telegramConfig;
    private final SeasonService seasonService;
    private final SeriesService seriesService;
    private final UserService userService;

    public SeriesCallbackHandler(ObjectMapper objectMapper, BotService botService, TelegramConfig telegramConfig, SeasonService seasonService, SeriesService seriesService, UserService userService) {
        this.objectMapper = objectMapper;
        this.botService = botService;
        this.telegramConfig = telegramConfig;
        this.seasonService = seasonService;
        this.seriesService = seriesService;
        this.userService = userService;
    }

    public void handle(User user, SeriesCallbackData callbackData) throws NoSuchSeriesException, NoSuchEpisodeException, JsonProcessingException {
        Optional<Series> optionalSeries = seriesService.findById(callbackData.seriesId);
        Series series;
        if (optionalSeries.isPresent()) {
            series = optionalSeries.get();
        } else {
            throw new NoSuchSeriesException("Такого сериала не существует. Попробуйте найти его заново.");
        }

        int seasonsCount = seasonService.countAllBySeries(series);
        int keyboardPageSeasonsLimit = telegramConfig.KEYBOARD_PAGE_SEASONS_MAX;
        int pagesCount = (int) Math.ceil(seasonsCount / (double) keyboardPageSeasonsLimit);

        List<Season> seriesSeasons
                = seasonService.findAllBySeriesOrderByNumberWithLimitAndPage(series, keyboardPageSeasonsLimit, callbackData.page);

        InlineKeyboardMarkup keyboard;
        if (seriesSeasons.isEmpty()) {
            throw new NoSuchEpisodeException("В сериале отсутствуют серии");
        } else {
            Map<String, String> keyboardMap = new LinkedHashMap<>();
            for (Season season : seriesSeasons) {
                keyboardMap.put("Сезон " + season.getNumber(), objectMapper.writeValueAsString(new SeasonCallbackData(season.getId(), 0)));
            }
            List<List<InlineKeyboardButton>> inlineKeyboard = TelegramBotUtils.createKeyboard(keyboardMap, 5, 2);

            if (pagesCount > 1) {
                List<InlineKeyboardButton> controlButtons = createControlButtons(series.getId(), pagesCount, callbackData.page);
                inlineKeyboard.add(controlButtons);
            }
            keyboard = new InlineKeyboardMarkup(inlineKeyboard);
        }

        String text = createText(series);

        MessageIds messagesIds = user.getMessageIds();
        if (messagesIds.hasSeasonMessageId()) {
            botService.deleteMessage(user, messagesIds.getSeasonMessageId());
            messagesIds.setSeasonMessageId(null);
        }
        if (messagesIds.hasEpisodeMessageId()) {
            botService.deleteMessage(user, messagesIds.getEpisodeMessageId());
            messagesIds.setEpisodeMessageId(null);
        }
        if (messagesIds.hasVideoMessageId()) {
            botService.deleteMessage(user, messagesIds.getVideoMessageId());
            messagesIds.setVideoMessageId(null);
        }

        if (messagesIds.hasSeriesMessageId()) {
            botService.editPhotoWithKeyboard(user
                    , messagesIds.getSeriesMessageId()
                    , keyboard
                    , series.getPreviewTgFileId());
        } else {
            Integer seriesMessageId = botService.sendPhotoWithMarkdownTextAndKeyboard(user
                    , text
                    , series.getPreviewTgFileId()
                    , keyboard);

            messagesIds.setSeriesMessageId(seriesMessageId);
        }

        userService.merge(user);
    }

    private static String createText(Series series) {
        return String.format("*%s*\n", series.getTitle())
                + '\n'
                + String.format("_%s_", series.getDescription());
    }

// TODO: 13.07.2023 create general control buttons generator
    public List<InlineKeyboardButton> createControlButtons(Long seriesId, int pagesCount, int currentPage) throws JsonProcessingException {
        InlineKeyboardButton previous;
        InlineKeyboardButton next;

        if (currentPage == 0) {
            previous = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                    .text("×").build();
        } else {
            previous = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new SeriesCallbackData(seriesId, currentPage - 1)))
                    .text("‹").build();
        }

        if (pagesCount == currentPage + 1) {
            next = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                    .text("×").build();
        } else {
            next = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new SeriesCallbackData(seriesId, currentPage + 1)))
                    .text("›").build();
        }

        InlineKeyboardButton current = InlineKeyboardButton.builder()
                .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                .text(currentPage + 1 + "/" + pagesCount).build();

        return Arrays.asList(previous, current, next);
    }
}