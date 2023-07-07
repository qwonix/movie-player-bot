package ru.qwonix.telegram.movieplayerbot.service.video;


import ru.qwonix.telegram.movieplayerbot.entity.Episode;
import ru.qwonix.telegram.movieplayerbot.entity.Movie;
import ru.qwonix.telegram.movieplayerbot.entity.Video;

import java.util.List;
import java.util.Optional;

public interface VideoService {

    Optional<Video> findById(Long id);

    Optional<Video> findMaxPriorityByEpisode(Episode episode);

    Optional<Video> findMaxPriorityByMovie(Movie movie);

    List<Video> findAllVideoByVideo(Video video);
}
