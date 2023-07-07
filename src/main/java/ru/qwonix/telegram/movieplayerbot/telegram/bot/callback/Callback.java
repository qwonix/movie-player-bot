package ru.qwonix.telegram.movieplayerbot.telegram.bot.callback;


import lombok.Data;


@Data
public class Callback {
    private final CallbackDataType type;
    private final CallbackData data;
}
