package ru.qwonix.telegram.movieplayerbot.telegram.bot.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@Data
@JsonTypeName
public class MovieCallbackData extends CallbackData {
    public final Long movieId;

    public MovieCallbackData(@JsonProperty("movieId") Long movieId) {
        this.movieId = movieId;
    }
}
