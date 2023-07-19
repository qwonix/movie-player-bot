package ru.qwonix.telegram.movieplayerbot.telegram.bot.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@Data
@JsonTypeName
public class SeasonCallbackData extends CallbackData {
    public final Long seasonId;
    public final Integer page;

    public SeasonCallbackData(@JsonProperty("seasonId") Long seasonId, @JsonProperty("page") Integer page) {
        this.seasonId = seasonId;
        this.page = page;
    }
}
