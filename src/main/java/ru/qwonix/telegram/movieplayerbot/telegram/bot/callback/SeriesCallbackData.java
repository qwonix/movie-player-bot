package ru.qwonix.telegram.movieplayerbot.telegram.bot.callback;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@Data
@JsonTypeName
public class SeriesCallbackData extends CallbackData {

    public final Long seriesId;
    public final Integer page;

}
