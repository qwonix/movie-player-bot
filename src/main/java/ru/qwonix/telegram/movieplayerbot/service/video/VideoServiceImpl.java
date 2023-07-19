package ru.qwonix.telegram.movieplayerbot.service.video;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwonix.telegram.movieplayerbot.entity.Episode;
import ru.qwonix.telegram.movieplayerbot.entity.Movie;
import ru.qwonix.telegram.movieplayerbot.entity.Video;
import ru.qwonix.telegram.movieplayerbot.repository.VideoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;

    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public Optional<Video> findById(Long id) {
        return videoRepository.findById(id);
    }

    @Override
    public Optional<Video> findMaxPriorityByEpisode(Episode episode) {
        return videoRepository.findFirstByEpisodeIdOrderByPriorityDesc(episode.getId());
    }

    @Override
    public Optional<Video> findMaxPriorityByMovie(Movie movie) {
        return videoRepository.findFirstByMovieIdOrderByPriorityDesc(movie.getId());
    }

    @Override
    @Transactional
    public List<Video> findAllVideoByVideo(Video video) {
        return videoRepository.findAllVideosByVideo(video);
    }
}
