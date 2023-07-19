package ru.qwonix.telegram.movieplayerbot.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwonix.telegram.movieplayerbot.entity.Season;
import ru.qwonix.telegram.movieplayerbot.entity.Series;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long>  {

    List<Season> findAllBySeriesOrderByNumber(Series series, Pageable pageable);

    Integer countAllBySeries(Series series);
}
