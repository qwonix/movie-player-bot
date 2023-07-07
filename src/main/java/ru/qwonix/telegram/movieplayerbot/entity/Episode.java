package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Builder(toBuilder = true)
@Getter
@Setter
public class Episode {
    private Long id;
    private String title;
    private String description;
    private Integer number;
    private Season season;
    private List<Video> videos;

    private String previewTgFileId;
}
