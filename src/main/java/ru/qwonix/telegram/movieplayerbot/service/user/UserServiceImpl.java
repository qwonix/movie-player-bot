package ru.qwonix.telegram.movieplayerbot.service.user;

import org.springframework.stereotype.Service;
import ru.qwonix.telegram.movieplayerbot.entity.User;
import ru.qwonix.telegram.movieplayerbot.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User makeAdmin(User user) {
        return null;
    }

    @Override
    public void merge(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findUser(Long userChatId) {
        return userRepository.findById(userChatId);
    }
}
