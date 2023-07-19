package ru.qwonix.telegram.movieplayerbot.service.series;

import org.springframework.stereotype.Service;
import ru.qwonix.telegram.movieplayerbot.entity.Series;
import ru.qwonix.telegram.movieplayerbot.repository.SeriesRepository;

import java.util.Optional;
@Service
public class SeriesServiceImpl implements  SeriesService{
    private final SeriesRepository seriesRepository;

    public SeriesServiceImpl(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    @Override
    public Optional<Series> findById(Long id) {
        return seriesRepository.findById(id);
    }
}
