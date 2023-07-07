package ru.qwonix.telegram.movieplayerbot.telegram.bot.state;

import org.springframework.stereotype.Component;
import ru.qwonix.telegram.movieplayerbot.service.telegram.BotService;
import ru.qwonix.telegram.movieplayerbot.service.user.UserService;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.handler.*;

@Component
public class SearchStateHandler extends DefaultStateHandler {
    private final UserService userService;
    private final BotService botService;

    public SearchStateHandler(VideoCallbackHandler videoCallbackHandler, EpisodeCallbackHandler episodeCallbackHandler, SeasonCallbackHandler seasonCallbackHandler, SeriesCallbackHandler seriesCallbackHandler, MovieCallbackHandler movieCallbackHandler, BotService botService, UserService userService) {
        super(botCommand, emptyCallbackHandler, videoCallbackHandler, episodeCallbackHandler, seasonCallbackHandler, seriesCallbackHandler, movieCallbackHandler, botService, userService);
        this.userService = userService;
        this.botService = botService;
    }

    @Override
    public void onText() {
        String query = update.getMessage().getText();
        botService.sendMarkdownText(user, String.format("Поиск по запросу: `%s`", query));
        user.setState(StateType.DEFAULT);
        userService.merge(user);
    }
}
