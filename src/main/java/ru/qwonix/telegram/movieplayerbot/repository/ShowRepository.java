package ru.qwonix.telegram.movieplayerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwonix.telegram.movieplayerbot.entity.Show;

import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

}
