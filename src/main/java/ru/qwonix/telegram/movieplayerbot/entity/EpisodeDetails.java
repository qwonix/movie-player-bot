package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "episode_details")
public class EpisodeDetails {
    @Id
    @Column(name = "episode_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "episode_id")
    private Episode episode;

    private String productionCode;
    private LocalDate releaseDate;
    private String releaseLanguage;
    private String releaseCountry;
//    @Column(name = "duration", columnDefinition = "interval")
//    private Duration duration;

    @Override
    public String toString() {
        return "EpisodeDetails{" +
                "id=" + id +
                ", productionCode='" + productionCode + '\'' +
                ", releaseDate=" + releaseDate +
                ", releaseLanguage='" + releaseLanguage + '\'' +
                ", releaseCountry='" + releaseCountry + '\'' +
                '}';
    }
}
