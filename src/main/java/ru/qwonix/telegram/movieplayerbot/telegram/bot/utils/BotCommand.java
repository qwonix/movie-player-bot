package ru.qwonix.telegram.movieplayerbot.telegram.bot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.qwonix.telegram.movieplayerbot.entity.*;
import ru.qwonix.telegram.movieplayerbot.service.episode.EpisodeService;
import ru.qwonix.telegram.movieplayerbot.service.movie.MovieService;
import ru.qwonix.telegram.movieplayerbot.service.season.SeasonService;
import ru.qwonix.telegram.movieplayerbot.service.series.SeriesService;
import ru.qwonix.telegram.movieplayerbot.service.telegram.BotService;
import ru.qwonix.telegram.movieplayerbot.service.user.UserService;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.MovieCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.SeasonCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.callback.SeriesCallbackData;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.handler.EmptyCallbackHandler;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.TelegramConfig;

import java.lang.reflect.Method;
import java.util.*;

@Component
@Slf4j
public class BotCommand {
    private static final Map<String, Method> METHOD_COMMAND = new HashMap<>();

    static {
        for (Method m : BotCommand.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                Command command = m.getAnnotation(Command.class);
                METHOD_COMMAND.put(command.value().toLowerCase(), m);
            }
        }
    }

    private final ObjectMapper objectMapper;
    private final TelegramConfig telegramConfig;
    private final BotService botService;
    private final UserService userService;
    private final MovieService movieService;
    private final SeriesService seriesService;
    private final EpisodeService episodeService;
    private final SeasonService seasonService;

    public BotCommand(ObjectMapper objectMapper, TelegramConfig telegramConfig, BotService botService, UserService userService, MovieService movieService, SeriesService seriesService, EpisodeService episodeService, SeasonService seasonService) {
        this.objectMapper = objectMapper;
        this.telegramConfig = telegramConfig;
        this.botService = botService;
        this.userService = userService;
        this.movieService = movieService;
        this.seriesService = seriesService;
        this.episodeService = episodeService;
        this.seasonService = seasonService;
    }

    public static Method getMethodForCommand(String command) {
        return METHOD_COMMAND.get(command);
    }

    @Command("/start")
    public void start(User user, String[] args) {
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(new KeyboardButton("/search (в разработке)"));
        keyboardButtons.add(new KeyboardButton("/all"));

        List<KeyboardRow> keyboardRows = Collections.singletonList(keyboardButtons);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setInputFieldPlaceholder("Выберите режим поиска");

        botService.sendMarkdownTextWithKeyBoard(user
                , "Вы можете найти нужную серию, используя команду /all"
                , replyKeyboardMarkup);

        log.info("start by {}", user);
    }

    @Command("/all")
    public void all(User user, String[] args) throws JsonProcessingException {
        botService.deleteMessageIds(user, user.getMessageIds());
        user.getMessageIds().reset();
        userService.merge(user);
        List<List<InlineKeyboardButton>> moviesKeyboard;
        {
            Map<String, String> keyboardMap = new LinkedHashMap<>();

            for (Movie movie : movieService.findByShow(Show.builder().id(1L).build())) {
                keyboardMap.put(movie.getTitle(), objectMapper.writeValueAsString(new MovieCallbackData(movie.getId())));
            }
            moviesKeyboard = TelegramBotUtils.createOneRowCallbackKeyboard(keyboardMap);
        }

        Optional<Series> optionalSeries = seriesService.findById(1L);
        Series series = optionalSeries.get();
        int page = 0;

        String text = String.format("*%s*\n", series.getTitle())
                + '\n'
                + String.format("_%s_", series.getDescription());

        List<List<InlineKeyboardButton>> seasonsKeyboard;

        {
            Map<String, String> keyboardMap = new LinkedHashMap<>();

            int seasonsCount = seasonService.countAllBySeries(series);
            int limit = telegramConfig.KEYBOARD_PAGE_SEASONS_MAX;
            int pagesCount = (int) Math.ceil(seasonsCount / (double) limit);
            List<Season> seriesSeasons = seasonService.findAllBySeriesOrderByNumberWithLimitAndPage(series, limit, page);

            for (Season season : seriesSeasons) {
                keyboardMap.put("Сезон " + season.getNumber(), new SeasonCallbackData(season.getId(), 0).toString());
            }
            seasonsKeyboard = TelegramBotUtils.createKeyboard(keyboardMap, 5, 2);

            if (pagesCount > 1) {
                List<InlineKeyboardButton> controlButtons = createControlButtons(series.getId(), pagesCount, page);
                seasonsKeyboard.add(controlButtons);
            }
        }

        moviesKeyboard.addAll(seasonsKeyboard);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(moviesKeyboard);

        Integer seriesMessageId = botService.sendPhotoWithMarkdownTextAndKeyboard(user
                , text
                , series.getPreviewTgFileId()
                , keyboard);


        /*List<Series> allSeries = databaseContext.getSeriesService().findAllOrdered();

        StringBuilder sb = new StringBuilder();
        if (allSeries.isEmpty()) {
            botUtils.sendMarkdownText(
                    user
                    , "У нас нету ни одного сериала или фильма, чтобы вам показать :(" +
                            "\n`Загляните попозже`");
        } else {
            Map<String, String> keyboard = new HashMap<>();
            for (Series series : allSeries) {
                String premiereReleaseYearOrTBA = series.getPremiereReleaseYearOrTBA();

                sb.append(String.format("`%s` – *%s* (%s)\n", series.getName(), series.getCountry(), premiereReleaseYearOrTBA));
                sb.append('\n');
                String description = series.getDescription()
                        .substring(0, series.getDescription().indexOf(' ', 90))
                        + "...";
                sb.append(String.format("_%s_\n", description));
                sb.append('\n');

                JSONObject seriesCallback = SeriesCallback.toJson(series.getId(), 0);
                keyboard.put(series.getName() + " (" + premiereReleaseYearOrTBA + ")", seriesCallback.toString());
                List<List<InlineKeyboardButton>> inlineKeyboard = BotUtils.createOneRowCallbackKeyboard(keyboard);
                botUtils.sendMarkdownTextWithKeyBoard(
                        user
                        , sb.toString()
                        , new InlineKeyboardMarkup(inlineKeyboard));
                user.getMessagesIds().reset();
                databaseContext.getUserService().merge(user);
            }
        }*/
    }

    @Command("/search")
    public void search(User user, String[] args) {
        botService.sendMarkdownText(user, "Поиск по названию находится в разработке :(");
        // botUtils.sendMarkdownText(user, "Введите название эпизода\nНапример: `Коллекционер`");
        // user.setStateType(State.StateType.SEARCH);
        // databaseContext.getUserService().merge(user);
    }


    @Command("/admin")
    public void admin(User user, String[] args) {
        if (args.length == 1) {
            String adminPassword = telegramConfig.ADMIN_PASSWORD;
            if (args[0].equals(adminPassword)) {
                user = userService.makeAdmin(user);
                botService.sendMarkdownText(user, "Вы получили права админа! /admin для доступа в меню");
                log.warn("became an admin: {}", user);
            } else {
                log.warn("trying to become an admin: {}", user);
            }
        }
    }

    @Command("/set_available")
    public void set_available(User user, String[] args) {
        if (!user.isAdmin()) {
            botService.sendMarkdownText(user, "Вы не являетесь администратором. Для получения прав используйте /admin <password>");
            return;
        }
        if (args.length == 2) {
            episodeService.setAvailableByEpisodeProductionCode(Integer.parseInt(args[0]), Boolean.valueOf(args[1]));
        }
    }

//    @Command("/export_video_videofileid")
//    public void export_video_videofileid(User user, String[] args) {
//        if (!user.isAdmin()) {
//            botUtils.sendMarkdownText(user, "Вы не являетесь администратором. Для получения прав используйте /admin <password>");
//            return;
//        }
//        for (Episode e : databaseContext.getEpisodeService().findAll()) {
//            botUtils.sendVideoWithMarkdownText(user, String.valueOf(e.getId()), e.getVideoFileId());
//        }
//        log.info("export by {}", user);
//    }
//
//    @Command("/export_video_previewfileid")
//    public void export_video_previewfileid(User user, String[] args) {
//        if (!user.isAdmin()) {
//            botUtils.sendMarkdownText(user, "Вы не являетесь администратором. Для получения прав используйте /admin <password>");
//            return;
//        }
//        for (Episode e : databaseContext.getEpisodeService().findAll()) {
//            botUtils.sendMarkdownTextWithPhoto(user, String.valueOf(e.getId()), e.getPreviewFileId());
//        }
//        log.info("export by {}", user);
//    }

   private List<InlineKeyboardButton> createControlButtons(Long seriesId, int pagesCount, int currentPage) throws JsonProcessingException {
        InlineKeyboardButton previous;
        InlineKeyboardButton next;

        if (currentPage == 0) {
            previous = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                    .text("×").build();
        } else {
            previous = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new SeriesCallbackData(seriesId, currentPage - 1)))
                    .text("‹").build();
        }

        if (pagesCount == currentPage + 1) {
            next = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                    .text("×").build();
        } else {
            next = InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(new SeriesCallbackData(seriesId, currentPage + 1)))
                    .text("›").build();
        }

        InlineKeyboardButton current = InlineKeyboardButton.builder()
                .callbackData(objectMapper.writeValueAsString(new EmptyCallbackHandler()))
                .text(currentPage + 1 + "/" + pagesCount).build();

        return Arrays.asList(previous, current, next);
    }

}