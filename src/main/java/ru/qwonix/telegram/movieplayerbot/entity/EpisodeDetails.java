package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class EpisodeDetails {
    private Long episodeId;
    private String productionCode;
    private LocalDate releaseDate;
    private String releaseLanguage;
    private String releaseCountry;
    private Duration duration;
}
