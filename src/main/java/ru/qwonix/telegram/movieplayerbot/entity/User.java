package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Builder;
import lombok.Data;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.state.StateType;

@Builder
@Data
public class User {

    private Long telegramId;
    private String firstName;

    private String lastName;
    private String username;
    private String languageCode;

    private boolean isAdmin;

    private StateType state;
    private MessageIds messageIds;
}
