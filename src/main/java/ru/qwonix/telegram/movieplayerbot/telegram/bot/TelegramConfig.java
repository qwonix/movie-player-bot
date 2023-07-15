package ru.qwonix.telegram.movieplayerbot.telegram.bot;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:telegram.properties")
public class TelegramConfig {

    @Value("${bot.token}")
    public String BOT_TOKEN;

    @Value("${bot.username}")
    public String BOT_USERNAME;

    @Value("${admin.password}")
    public String ADMIN_PASSWORD;

    @Value("${keyboard.page.episodes.max}")
    public Integer KEYBOARD_PAGE_EPISODES_MAX;

    @Value("${keyboard.page.seasons.max}")
    public Integer KEYBOARD_PAGE_SEASONS_MAX;

    @Value("${keyboard.columns.row.max}")
    public Integer KEYBOARD_COLUMNS_ROW_MAX;


}
