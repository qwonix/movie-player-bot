package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class MessageIds {
    private Integer seriesMessageId;
    private Integer seasonMessageId;
    private Integer episodeMessageId;
    private Integer videoMessageId;

// TODO: 03.07.2023 when deserializes check fields not null

    public boolean hasSeriesMessageId() {
        return seriesMessageId != null;
    }

    public boolean hasSeasonMessageId() {
        return seasonMessageId != null;
    }

    public boolean hasEpisodeMessageId() {
        return episodeMessageId != null;
    }

    public boolean hasVideoMessageId() {
        return videoMessageId != null;
    }

    public void clear() {
        this.setSeriesMessageId(null);
        this.setSeasonMessageId(null);
        this.setEpisodeMessageId(null);
        this.setVideoMessageId(null);
    }
}
