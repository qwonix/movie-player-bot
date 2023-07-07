package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Builder(toBuilder = true)
@Data
public class Show {
    private Integer id;
    private String description;
    private String title;

    private String previewTgFileId;
}
