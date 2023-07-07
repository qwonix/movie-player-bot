package ru.qwonix.telegram.movieplayerbot.service.movie;


import ru.qwonix.telegram.movieplayerbot.entity.Movie;
import ru.qwonix.telegram.movieplayerbot.entity.Show;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    Optional<Movie> find(Long id);

    List<Movie> findByShow(Show show);

}
