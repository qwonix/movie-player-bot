package ru.qwonix.telegram.movieplayerbot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "episode")
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Integer number;

    @OneToOne(mappedBy = "episode", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private EpisodeDetails episodeDetails;

    // FIXME: 16.07.2023 choose fetch type
    @ManyToOne
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @OneToMany
    private List<Video> videos;

    private String previewTgFileId;
}
