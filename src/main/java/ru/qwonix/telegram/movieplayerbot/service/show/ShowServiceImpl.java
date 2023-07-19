package ru.qwonix.telegram.movieplayerbot.service.show;

import org.springframework.stereotype.Service;
import ru.qwonix.telegram.movieplayerbot.entity.Show;
import ru.qwonix.telegram.movieplayerbot.repository.ShowRepository;

import java.util.Optional;

@Service
public class ShowServiceImpl implements ShowService {
    private final ShowRepository showRepository;

    public ShowServiceImpl(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    @Override
    public Optional<Show> findById(Long id) {
        return showRepository.findById(id);
    }
}
