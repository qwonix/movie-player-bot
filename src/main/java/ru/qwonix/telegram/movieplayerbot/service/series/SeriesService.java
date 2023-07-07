package ru.qwonix.telegram.movieplayerbot.service.series;


import ru.qwonix.telegram.movieplayerbot.entity.Series;

import java.util.Optional;

public interface SeriesService {
    Optional<Series> findById(Long id);
}
