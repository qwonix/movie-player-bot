package ru.qwonix.telegram.movieplayerbot.telegram.bot.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.qwonix.telegram.movieplayerbot.entity.MessageIds;
import ru.qwonix.telegram.movieplayerbot.entity.User;
import ru.qwonix.telegram.movieplayerbot.entity.Video;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchVideoException;
import ru.qwonix.telegram.movieplayerbot.service.telegram.BotService;
import ru.qwonix.telegram.movieplayerbot.service.user.UserService;
import ru.qwonix.telegram.movieplayerbot.service.video.VideoService;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.VideoCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.config.TelegramConfig;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.utils.TelegramBotUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class VideoCallbackHandler {
    private final ObjectMapper objectMapper;
    private final BotService botService;

    private final TelegramConfig telegramConfig;

    private final UserService userService;

    private final VideoService videoService;

    public VideoCallbackHandler(ObjectMapper objectMapper, BotService botService, TelegramConfig telegramConfig, UserService userService, VideoService videoService) {
        this.objectMapper = objectMapper;
        this.botService = botService;
        this.telegramConfig = telegramConfig;
        this.userService = userService;
        this.videoService = videoService;
    }

    public void handle(User user, VideoCallbackData callbackData) throws NoSuchVideoException {
        Optional<Video> optionalVideo = videoService.findById(callbackData.videoId);
        Video video;
        if (optionalVideo.isPresent()) {
            video = optionalVideo.get();
        } else {
            throw new NoSuchVideoException("Такого видео не существует. Попробуйте найти его заново.");
        }

        List<Video> videos = videoService.findAllVideoByVideo(video);
        videos.remove(video);

        List<List<InlineKeyboardButton>> buttons = createVideosButtons(videos);

        InlineKeyboardMarkup keyboard = null;
        if (!buttons.isEmpty()) {
            keyboard = new InlineKeyboardMarkup(buttons);
        }

        MessageIds messagesIds = user.getMessageIds();
        if (messagesIds.hasVideoMessageId()) {
            botService.editVideoWithMarkdownTextAndKeyboard(user
                    , messagesIds.getVideoMessageId()
                    , botService.getProvidedByText()
                    , video.getVideoTgFileId()
                    , keyboard);
        } else {
            Integer videoMessageId = botService.sendVideoWithMarkdownTextAndKeyboard(user
                    , botService.getProvidedByText()
                    , video.getVideoTgFileId()
                    , keyboard);
            messagesIds.setVideoMessageId(videoMessageId);
        }

        userService.merge(user);
    }


    private List<List<InlineKeyboardButton>> createVideosButtons(List<Video> episodeVideos) {
        Map<String, String> keyboardMap = new LinkedHashMap<>();
        for (Video video : episodeVideos) {
            String text = String.format("%dр subs:%s dub:%s", video.getResolution(), video.getSubtitlesLanguage(), video.getAudioLanguage());

            try {
                keyboardMap.put(text, objectMapper.writeValueAsString(new VideoCallbackData(video.getId())));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return TelegramBotUtils.createOneRowCallbackKeyboard(keyboardMap);
    }
}
