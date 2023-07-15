package ru.qwonix.telegram.movieplayerbot.telegram.bot;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.qwonix.telegram.movieplayerbot.entity.MessageIds;
import ru.qwonix.telegram.movieplayerbot.entity.User;
import ru.qwonix.telegram.movieplayerbot.service.user.UserService;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.state.DefaultStateHandler;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.state.State;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.state.StateType;

import java.util.Optional;

@Component
public class MoviePlayerBot extends TelegramLongPollingBot {
    private final TelegramConfig telegramConfig;
    private final DefaultStateHandler defaultStateHandler;
    private final UserService userService;

    public MoviePlayerBot(TelegramConfig telegramConfig, @Lazy DefaultStateHandler defaultStateHandler, UserService userService) {
        super(telegramConfig.BOT_TOKEN);
        this.telegramConfig = telegramConfig;
        this.defaultStateHandler = defaultStateHandler;
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        org.telegram.telegrambots.meta.api.objects.User telegramUser;

        if (update.hasCallbackQuery()) {
            telegramUser = update.getCallbackQuery().getFrom();
        } else if (update.hasMessage()) {
            telegramUser = update.getMessage().getFrom();
        } else {
            throw new IllegalArgumentException("update has no user");
        }

        Optional<User> optionalUser = userService.findUser(telegramUser.getId());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = User.builder()
                    .telegramId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .username(telegramUser.getUserName())
                    .languageCode(telegramUser.getLanguageCode())
                    .isAdmin(false)
                    .messageIds(new MessageIds())
                    .state(StateType.DEFAULT)
                    .build();
            userService.merge(user);
        }

        State stateHandler;
        switch (user.getState()) {
            case DEFAULT:
                stateHandler = defaultStateHandler;
                break;
            default:
                stateHandler = defaultStateHandler;
                break;
        }

        stateHandler.setUpdate(update);
        stateHandler.setUser(user);

        if (update.hasCallbackQuery()) {
            stateHandler.onCallback();
        } else if (!update.hasMessage()) {
        } else if (update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/")) {
                stateHandler = defaultStateHandler;
                stateHandler.onText();
            }

        } else if (update.getMessage().hasVideo()) {
            stateHandler.onVideo();
        } else if (update.getMessage().hasPhoto()) {
            stateHandler.onPhoto();
        }
    }

    @Override
    public String getBotUsername() {
        return telegramConfig.BOT_USERNAME;
    }
}
