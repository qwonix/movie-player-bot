package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String resolution;
    private String audioLanguage;
    private String subtitlesLanguage;
    private Integer priority;

    private String videoTgFileId;

    @ManyToOne
    @JoinColumn(name = "episode_id")
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

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
