package ru.qwonix.telegram.movieplayerbot.telegram.bot.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SeriesCallbackData extends CallbackData {
    public SeriesCallbackData(@JsonProperty("seriesId") Long seriesId, @JsonProperty("page") Integer page) {
        this.seriesId = seriesId;
        this.page = page;
    }

    public final Long seriesId;
    public final Integer page;

}
