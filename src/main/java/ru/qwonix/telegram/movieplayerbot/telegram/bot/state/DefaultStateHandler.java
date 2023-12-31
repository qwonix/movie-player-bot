package ru.qwonix.telegram.movieplayerbot.telegram.bot.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchCallbackException;
import ru.qwonix.telegram.movieplayerbot.service.telegram.BotService;
import ru.qwonix.telegram.movieplayerbot.service.user.UserService;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.*;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.handler.*;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.utils.BotCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class DefaultStateHandler extends State {

    private final BotCommand botCommand;
    private final VideoCallbackHandler videoCallbackHandler;
    private final EpisodeCallbackHandler episodeCallbackHandler;
    private final SeasonCallbackHandler seasonCallbackHandler;
    private final SeriesCallbackHandler seriesCallbackHandler;
    private final MovieCallbackHandler movieCallbackHandler;

    private final BotService botService;

    public DefaultStateHandler(BotCommand botCommand, VideoCallbackHandler videoCallbackHandler, EpisodeCallbackHandler episodeCallbackHandler, SeasonCallbackHandler seasonCallbackHandler, SeriesCallbackHandler seriesCallbackHandler, MovieCallbackHandler movieCallbackHandler, BotService botService, UserService userService) {
        this.botCommand = botCommand;
        this.videoCallbackHandler = videoCallbackHandler;
        this.episodeCallbackHandler = episodeCallbackHandler;
        this.seasonCallbackHandler = seasonCallbackHandler;
        this.seriesCallbackHandler = seriesCallbackHandler;
        this.movieCallbackHandler = movieCallbackHandler;
        this.botService = botService;
    }

    @Override
    public void onCallback() {
        String jsonCallbackData = update.getCallbackQuery().getData();
        String callbackId = update.getCallbackQuery().getId();
        CallbackData callbackData;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            callbackData = objectMapper.readValue(jsonCallbackData, CallbackData.class);
        } catch (Exception e) {
            return;
        }
        try {
            if (callbackData instanceof VideoCallbackData) {
                videoCallbackHandler.handle(user, (VideoCallbackData) callbackData);
            } else if (callbackData instanceof EpisodeCallbackData) {
                episodeCallbackHandler.handle(user, (EpisodeCallbackData) callbackData);
            } else if (callbackData instanceof SeasonCallbackData) {
                seasonCallbackHandler.handle(user, (SeasonCallbackData) callbackData);
            } else if (callbackData instanceof SeriesCallbackData) {
                seriesCallbackHandler.handle(user, (SeriesCallbackData) callbackData);
            } else if (callbackData instanceof MovieCallbackData) {
                movieCallbackHandler.handle(user, (MovieCallbackData) callbackData);
            }
        } catch (NoSuchCallbackException e) {
            throw new RuntimeException(e);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("user {} callback {}", user, callbackData);
        botService.executeAlert(callbackId, false);
    }

    @Override
    public void onVideo() {
        if (!user.isAdmin()) {
            botService.sendMarkdownText(user
                    , "Вы не являетесь администратором. Для получения прав используйте /admin <password>");
            return;
        }
        Video video = update.getMessage().getVideo();

        log.info("user {} send video {}", user, video);

        if (user.isAdmin()) {
            botService.sendMarkdownTextWithReplay(user
                    , "getFileId: `" + video.getFileId() + "`" + '\n' + "duration (sec): `" + video.getDuration() + "`"
                    , update.getMessage().getMessageId());
        } else {
            botService.sendMarkdownText(user
                    , "Вы не являетесь администратором. Для получения прав используйте /admin <password>");

        }
    }

    @Override
    public void onPhoto() {
        if (!user.isAdmin()) {
            botService.sendMarkdownText(user
                    , "Вы не являетесь администратором. Для получения прав используйте /admin <password>");
            return;
        }

        List<PhotoSize> photos = update.getMessage().getPhoto();
        log.info("user {} send {} photos", user, photos.size());
        PhotoSize photoSize = photos.stream().max(Comparator.comparingInt(PhotoSize::getFileSize)).get();

        botService.sendMarkdownTextWithReplay(user
                , "`" + photoSize.getFileId() + "`"
                , update.getMessage().getMessageId());

    }

    @Override
    public void onText() {
        String userMessageText = update.getMessage().getText();
        log.info("user {} send text {}", user, userMessageText);

        String[] allArgs = userMessageText.split(" ");
        String command = allArgs[0].toLowerCase();
        String[] commandArgs = Arrays.copyOfRange(allArgs, 1, allArgs.length);

        try {
            Method commandMethod = BotCommand.getMethodForCommand(command);

            if (commandMethod != null) {
                commandMethod.invoke(botCommand, user, commandArgs);
                return;
            }

            botService.sendMarkdownText(user, "Используйте команды и кнопки, бот не имеет интерфейса общения");
        } catch (IllegalAccessException e) {
            log.error("reflective access exception", e);
        } catch (InvocationTargetException e) {
            log.error("called method-command threw an exception", e);
        }
    }
}
