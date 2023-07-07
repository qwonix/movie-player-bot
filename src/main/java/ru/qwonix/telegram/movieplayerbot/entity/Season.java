package ru.qwonix.telegram.movieplayerbot.entity;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@ToString
@Builder(toBuilder = true)
@Data
public class Season {
    private Long id;
    private Integer number;
    private String description;
    private Integer totalEpisodesCount;
    private Series series;

    private String previewTgFileId;

    private LocalDate premiereReleaseDate;
    private LocalDate finalReleaseDate;

    public String getFormattedPremiereReleaseDate() {
        String seriesPremiereReleaseDate;

        if (this.getPremiereReleaseDate() == null) {
            seriesPremiereReleaseDate = "TBA";
        } else {
            seriesPremiereReleaseDate = this.getPremiereReleaseDate()
                    .format(DateTimeFormatter.ofPattern("d MMMM y", Locale.forLanguageTag("ru")))
                    + " года";
        }
        return seriesPremiereReleaseDate;
    }

    public String getFormattedFinalReleaseDate() {
        String seriesFinalReleaseDate;
        if (this.getFinalReleaseDate() == null) {
            seriesFinalReleaseDate = "TBA";
        } else {
            seriesFinalReleaseDate = this.getFinalReleaseDate()
                    .format(DateTimeFormatter.ofPattern("d MMMM y", Locale.forLanguageTag("ru")))
                    + " года";
        }
        return seriesFinalReleaseDate;
    }
}
