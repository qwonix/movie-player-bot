package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@ToString
@Builder(toBuilder = true)
@Data
public class Series {
    private Long id;
    private String title;
    private String description;
    private Show show;

    private String previewTgFileId;
}
