package ru.qwonix.telegram.movieplayerbot.service.episode;


import ru.qwonix.telegram.movieplayerbot.entity.Episode;
import ru.qwonix.telegram.movieplayerbot.entity.Season;

import java.util.List;
import java.util.Optional;

public interface EpisodeService {
    Optional<Episode> findById(Long id);

    Optional<Episode> findNext(Episode episode);

    Optional<Episode> findPrevious(Episode episode);

    List<Episode> findAllBySeasonOrderByNumberWithLimitAndPage(Season season, int limit, int page);

    int countAllBySeason(Season season);

    void setAvailableByEpisodeProductionCode(int episodeProductionCode, Boolean isAvailable);
}
