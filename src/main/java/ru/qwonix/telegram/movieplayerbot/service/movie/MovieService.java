package ru.qwonix.telegram.movieplayerbot.service.movie;


import ru.qwonix.telegram.movieplayerbot.entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    Optional<Movie> findById(Long id);

    List<Movie> findAllByShowId(Long showId);

}
