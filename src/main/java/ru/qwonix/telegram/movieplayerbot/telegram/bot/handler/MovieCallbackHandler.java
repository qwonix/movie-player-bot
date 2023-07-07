package ru.qwonix.telegram.movieplayerbot.telegram.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.qwonix.telegram.movieplayerbot.entity.MessageIds;
import ru.qwonix.telegram.movieplayerbot.entity.Movie;
import ru.qwonix.telegram.movieplayerbot.entity.User;
import ru.qwonix.telegram.movieplayerbot.entity.Video;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchMovieException;
import ru.qwonix.telegram.movieplayerbot.exception.NoSuchVideoException;
import ru.qwonix.telegram.movieplayerbot.service.movie.MovieService;
import ru.qwonix.telegram.movieplayerbot.service.telegram.BotService;
import ru.qwonix.telegram.movieplayerbot.service.user.UserService;
import ru.qwonix.telegram.movieplayerbot.service.video.VideoService;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.MovieCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.VideoCallbackData;


import java.util.Optional;

@Component
@Slf4j
public class MovieCallbackHandler {
    private final BotService botService;
    private final UserService userService;
    private final MovieService movieService;
    private final VideoService videoService;
    private final VideoCallbackHandler videoCallbackHandler;


    public MovieCallbackHandler(BotService botService, UserService userService, MovieService movieService, VideoService videoService, VideoCallbackHandler videoCallbackHandler) {
        this.botService = botService;
        this.userService = userService;
        this.movieService = movieService;
        this.videoService = videoService;
        this.videoCallbackHandler = videoCallbackHandler;
    }


    public void handle(User user, MovieCallbackData callbackData) throws NoSuchMovieException, NoSuchVideoException {
        Optional<Movie> optionalMovie = movieService.findById(callbackData.movieId);
        Movie movie;
        if (optionalMovie.isPresent()) {
            movie = optionalMovie.get();
        } else {
            throw new NoSuchMovieException("Такого фильма не существует. Попробуйте найти его заново.");
        }

        Optional<Video> maxPriorityOptionalVideo = videoService.findMaxPriorityByMovie(movie);

        Video maxPriorityVideo;
        if (maxPriorityOptionalVideo.isPresent()) {
            maxPriorityVideo = maxPriorityOptionalVideo.get();
        } else {
            throw new NoSuchVideoException("Видео не найдено. Попробуйте заново.");
        }

        String movieText = createText(movie);

        MessageIds messagesIds = user.getMessageIds();
        if (messagesIds.hasSeasonMessageId()) {
            botService.deleteMessage(user, messagesIds.getSeasonMessageId());
            messagesIds.setSeasonMessageId(null);
        }
        if (messagesIds.hasSeriesMessageId()) {
            botService.deleteMessage(user, messagesIds.getSeriesMessageId());
            messagesIds.setSeriesMessageId(null);
        }

        if (messagesIds.hasEpisodeMessageId()) {
            botService.editPhotoWithMarkdownText(user
                    , messagesIds.getEpisodeMessageId()
                    , movieText
                    , movie.getPreviewTgFileId());
        } else {
            Integer movieMessageId = botService.sendPhotoWithMarkdownText(user
                    , movieText
                    , movie.getPreviewTgFileId());
            messagesIds.setEpisodeMessageId(movieMessageId);
        }

        videoCallbackHandler.handle(user, new VideoCallbackData(maxPriorityVideo.getId()));

        userService.merge(user);
    }

    private static String createText(Movie movie) {
        return String.format("*%s*\n", movie.getTitle())
                + '\n'
                + String.format("_%s_", movie.getDescription());
    }
}