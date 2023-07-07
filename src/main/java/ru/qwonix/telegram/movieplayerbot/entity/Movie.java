package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Builder(toBuilder = true)
@Data
public class Movie {
    private Long id;
    private String title;
    private String description;
    private Show show;
    private List<Video> videos;

    private String previewTgFileId;
}
