package ru.qwonix.telegram.movieplayerbot.telegram.bot.state;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.qwonix.telegram.movieplayerbot.entity.User;

public abstract class State {

    protected User user;
    protected Update update;

    public void setUser(User user) {
        this.user = user;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    abstract public void onVideo();

    abstract public void onCallback();

    abstract public void onPhoto();

    abstract public void onText();
}
