package ru.qwonix.telegram.movieplayerbot.telegram.bot.callback;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@class")
public abstract class CallbackData {

}