package ru.qwonix.telegram.movieplayerbot.service.user;


import ru.qwonix.telegram.movieplayerbot.entity.User;

import java.util.Optional;

public interface UserService {
    User makeAdmin(User user);

    void merge(User user);

    Optional<User> findUser(Long userChatId);
}
