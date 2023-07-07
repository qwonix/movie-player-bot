package ru.qwonix.telegram.movieplayerbot.service.show;


import ru.qwonix.telegram.movieplayerbot.entity.Show;

import java.util.Optional;

public interface ShowService {
    Optional<Show> find(int id);
}
