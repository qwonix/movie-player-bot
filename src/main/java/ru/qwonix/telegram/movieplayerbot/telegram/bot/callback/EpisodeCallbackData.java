package ru.qwonix.telegram.movieplayerbot.telegram.bot.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@Data
@JsonTypeName
public class EpisodeCallbackData extends CallbackData {
    public final Long episodeId;

    public EpisodeCallbackData(@JsonProperty("episodeId") Long episodeId) {
        this.episodeId = episodeId;
    }
}
