package ru.qwonix.telegram.movieplayerbot.service.season;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.qwonix.telegram.movieplayerbot.entity.Season;
import ru.qwonix.telegram.movieplayerbot.entity.Series;
import ru.qwonix.telegram.movieplayerbot.repository.SeasonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;

    public SeasonServiceImpl(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    @Override
    public Optional<Season> findById(Long id) {
        return seasonRepository.findById(id);
    }

    @Override
    public List<Season> findAllBySeriesOrderByNumberWithLimitAndPage(Series series, int limit, int page) {
        return seasonRepository.findAllBySeriesOrderByNumber(series, PageRequest.of(page, limit));
    }

    @Override
    public int countAllBySeries(Series series) {
        return seasonRepository.countAllBySeries(series);
    }
}
