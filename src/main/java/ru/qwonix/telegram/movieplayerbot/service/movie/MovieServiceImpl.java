package ru.qwonix.telegram.movieplayerbot.service.movie;

import org.springframework.stereotype.Service;
import ru.qwonix.telegram.movieplayerbot.entity.Movie;
import ru.qwonix.telegram.movieplayerbot.repository.MovieRepository;

import java.util.List;
import java.util.Optional;
@Service
public class MovieServiceImpl implements MovieService{
    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public List<Movie> findAllByShowId(Long showId) {
        return movieRepository.findAllByShowId(showId);
    }


}
