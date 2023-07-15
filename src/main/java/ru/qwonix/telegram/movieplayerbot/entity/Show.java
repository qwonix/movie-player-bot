package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@Data
public class Show {
    private Long id;
    private String description;
    private String title;

    private String previewTgFileId;
}
