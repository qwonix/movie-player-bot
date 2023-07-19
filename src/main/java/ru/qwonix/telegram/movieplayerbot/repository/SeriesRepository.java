package ru.qwonix.telegram.movieplayerbot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwonix.telegram.movieplayerbot.entity.Series;


@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

}
