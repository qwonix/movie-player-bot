package ru.qwonix.telegram.movieplayerbot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.qwonix.telegram.movieplayerbot.entity.Episode;
import ru.qwonix.telegram.movieplayerbot.entity.Video;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findAllByEpisodeId(Long episodeId);

    List<Video> findAllByMovieId(Long episodeId);

    Optional<Video> findFirstByEpisodeIdOrderByPriorityDesc(Long episodeId);

    Optional<Video> findFirstByMovieIdOrderByPriorityDesc(Long episodeId);

    @Query("select v.episode.videos from Video v where v = ?1 ")
    List<Video>  findAllVideosByVideo(Video video);
}