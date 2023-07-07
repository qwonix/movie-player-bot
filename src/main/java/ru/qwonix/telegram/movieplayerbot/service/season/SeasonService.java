package ru.qwonix.telegram.movieplayerbot.service.season;


import ru.qwonix.telegram.movieplayerbot.entity.Season;
import ru.qwonix.telegram.movieplayerbot.entity.Series;

import java.util.List;
import java.util.Optional;

public interface SeasonService {
    Optional<Season> find(Long id);

    List<Season> findAllBySeriesOrderByNumberWithLimitAndPage(Series series, int limit, int page);

    int countAllBySeries(Series series);

}
