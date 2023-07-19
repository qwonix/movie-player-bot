package ru.qwonix.telegram.movieplayerbot.service.episode;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.qwonix.telegram.movieplayerbot.entity.Episode;
import ru.qwonix.telegram.movieplayerbot.entity.Season;
import ru.qwonix.telegram.movieplayerbot.repository.EpisodeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeRepository episodeRepository;

    public EpisodeServiceImpl(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    @Override
    public Optional<Episode> findById(Long id) {
        return episodeRepository.findById(id);
    }

    @Override
    public List<Episode> findAllBySeasonOrderByNumberWithLimitAndPage(Season season, int limit, int page) {
        return episodeRepository.findAllBySeasonIdOrderByNumber(season.getId(), PageRequest.of(page, limit));
    }

    @Override
    public int countAllBySeason(Season season) {
        return episodeRepository.countAllBySeason(season);
    }

}
