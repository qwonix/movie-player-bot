package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;


@Data
@Entity
@Table(name = "series")
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "show_id")
    private Show show;

    private String previewTgFileId;
}
