package ru.qwonix.telegram.movieplayerbot.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwonix.telegram.movieplayerbot.entity.Episode;
import ru.qwonix.telegram.movieplayerbot.entity.Season;

import java.util.List;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    List<Episode> findAllBySeasonIdOrderByNumber(Long seasonId, Pageable pageable);

    Integer countAllBySeason(Season season);

}