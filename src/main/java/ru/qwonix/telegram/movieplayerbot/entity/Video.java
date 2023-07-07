package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Builder(toBuilder = true)
@Getter
@Setter
public class Video {
    private Long id;
    private String resolution;
    private String audioLanguage;
    private String subtitlesLanguage;
    private Integer priority;

    private String videoTgFileId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(videoTgFileId, video.videoTgFileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoTgFileId);
    }
}
