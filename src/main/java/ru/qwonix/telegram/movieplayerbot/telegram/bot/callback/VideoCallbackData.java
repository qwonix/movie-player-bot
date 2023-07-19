package ru.qwonix.telegram.movieplayerbot.telegram.bot.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VideoCallbackData extends CallbackData {
    public final Long videoId;

    public VideoCallbackData(@JsonProperty("videoId") Long videoId) {
        this.videoId = videoId;
    }
}
