package ru.qwonix.telegram.movieplayerbot.telegram.bot.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.qwonix.telegram.movieplayerbot.entity.Episode;
import ru.qwonix.telegram.movieplayerbot.entity.MessageIds;
import ru.qwonix.telegram.movieplayerbot.entity.User;
import ru.qwonix.telegram.movieplayerbot.entity.Video;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchEpisodeException;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchVideoException;
import ru.qwonix.telegram.movieplayerbot.service.episode.EpisodeService;
import ru.qwonix.telegram.movieplayerbot.service.telegram.BotService;
import ru.qwonix.telegram.movieplayerbot.service.user.UserService;
import ru.qwonix.telegram.movieplayerbot.service.video.VideoService;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.EpisodeCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.VideoCallbackData;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class EpisodeCallbackHandler {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final VideoCallbackHandler videoCallbackHandler;

    private final BotService botService;
    private final EpisodeService episodeService;
    private final VideoService videoService;

    public EpisodeCallbackHandler(UserService userService, ObjectMapper objectMapper, VideoCallbackHandler videoCallbackHandler, BotService botService, EpisodeService episodeService, VideoService videoService) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.videoCallbackHandler = videoCallbackHandler;
        this.botService = botService;
        this.episodeService = episodeService;
        this.videoService = videoService;
    }

    public void handle(User user, EpisodeCallbackData episodeCallbackData) throws NoSuchEpisodeException, NoSuchVideoException, JsonProcessingException {
        Optional<Episode> optionalEpisode = episodeService.findById(episodeCallbackData.episodeId);
        Episode episode;
        if (optionalEpisode.isPresent()) {
            episode = optionalEpisode.get();
        } else {
            throw new NoSuchEpisodeException("Такого эпизода не существует. Попробуйте найти его заново.");
        }
        Optional<Video> maxPriorityOptionalVideo = videoService.findMaxPriorityByEpisode(episode);

        Video maxPriorityVideo;
        if (maxPriorityOptionalVideo.isPresent()) {
            maxPriorityVideo = maxPriorityOptionalVideo.get();
        } else {
            throw new NoSuchVideoException("Видео не найдено. Попробуйте заново.");
        }

        Optional<Episode> nextEpisode = Optional.ofNullable(episode.getNext());
        Optional<Episode> previousEpisode = Optional.ofNullable(episode.getPrevious());
        int seasonEpisodesCount = episodeService.countAllBySeason(episode.getSeason());

        List<List<InlineKeyboardButton>> controlButtons
                = createControlButtons(episode, nextEpisode, previousEpisode, seasonEpisodesCount);

        String episodeText = createText(episode);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(controlButtons);

        MessageIds messagesIds = user.getMessageIds();
        if (messagesIds.hasEpisodeMessageId()) {
            botService.editPhotoWithMarkdownTextAndKeyboard(user
                    , messagesIds.getEpisodeMessageId()
                    , episodeText
                    , episode.getPreviewTgFileId()
                    , keyboard);
        } else {
            Integer episodeMessageId = botService.sendPhotoWithMarkdownTextAndKeyboard(user
                    , episodeText
                    , episode.getPreviewTgFileId()
                    , keyboard
            );
            messagesIds.setEpisodeMessageId(episodeMessageId);
        }


        videoCallbackHandler.handle(user, new VideoCallbackData(maxPriorityVideo.getId()));

        userService.merge(user);
    }

    private static String createText(Episode episode) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM y", Locale.forLanguageTag("ru"));

        return String.format("_%s сезон %s серия_ – `%s`\n", episode.getSeason().getNumber(), episode.getNumber(), episode.getTitle())
                + '\n'
                + String.format("_%s_\n", episode.getDescription())
                + '\n'
//                + String.format("*Дата выхода:* `%s года`\n", episode.getReleaseDate().format(dateTimeFormatter))
//                + String.format("*Страна:* `%s` (_%s_)", episode.getCountry(), episode.getLanguage())
                ;
    }


    public List<List<InlineKeyboardButton>> createControlButtons(
            Episode currentEpisode
            , Optional<Episode> nextEpisode
            , Optional<Episode> previousEpisode
            , int seasonEpisodesCount) throws JsonProcessingException {
        InlineKeyboardButton previous;
        InlineKeyboardButton next;

        if (nextEpisode.isPresent()) {
            next = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new EpisodeCallbackData(nextEpisode.get().getId())))
                    .text("›").build();
        } else {
            next = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                    .text("×").build();
        }

        if (previousEpisode.isPresent()) {
            previous = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new EpisodeCallbackData(previousEpisode.get().getId())))
                    .text("‹").build();
        } else {
            previous = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                    .text("×").build();
        }

        InlineKeyboardButton current = InlineKeyboardButton.builder()
                .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                .text(currentEpisode.getNumber() + "/" + seasonEpisodesCount).build();

        return new ArrayList<>(Collections.singletonList(Arrays.asList(previous, current, next)));
    }

}

