package ru.qwonix.telegram.movieplayerbot.telegram.bot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TelegramBotUtils {


    public static List<List<InlineKeyboardButton>> createOneRowCallbackKeyboard(Map<String, String> buttons) {
        return convertToInlineKeyboardButtonsStream(buttons).map(Arrays::asList).collect(Collectors.toList());
    }

    private static Stream<InlineKeyboardButton> convertToInlineKeyboardButtonsStream(Map<String, String> buttons) {
        return buttons.entrySet().stream().map(pair ->
                InlineKeyboardButton.builder()
                        .text(pair.getKey())
                        .callbackData(pair.getValue())
                        .build());
    }


//    public static List<List<InlineKeyboardButton>> createTwoRowsCallbackKeyboard(Map<String, String> buttons) {
//        if (buttons.size() < BotConfig.getIntProperty(BotConfig.KEYBOARD_COLUMNS_ROW_MAX)) {
//            return createOneRowCallbackKeyboard(buttons);
//        }
//        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
//
//        int l = buttons.size() / 2;
//        List<InlineKeyboardButton> firstPart = convertToCallbackButtons(buttons).limit(l).collect(Collectors.toList());
//        List<InlineKeyboardButton> secondPart = convertToCallbackButtons(buttons).skip(l).collect(Collectors.toList());
//
//        for (int i = 0; i < l; i++) {
//            List<InlineKeyboardButton> rowInline = new ArrayList<>();
//
//            rowInline.add(firstPart.get(i));
//            rowInline.add(secondPart.get(i));
//            rowsInline.add(rowInline);
//        }
//
//        if (buttons.size() % 2 == 1) {
//            List<InlineKeyboardButton> rowInline = new ArrayList<>();
//            rowInline.add(secondPart.get(l));
//            rowsInline.add(rowInline);
//        }
//        return rowsInline;
//    }

    public static List<List<InlineKeyboardButton>> createKeyboard(Map<String, String> buttons, final int rowCount, final int columnCount) {
        if (buttons.size() <= rowCount) {
            return createOneRowCallbackKeyboard(buttons);
        }
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        int rowsInColumnCount = buttons.size() / columnCount;
        List<InlineKeyboardButton> firstPart = convertToInlineKeyboardButtonsStream(buttons).limit(rowsInColumnCount).collect(Collectors.toList());
        List<InlineKeyboardButton> secondPart = convertToInlineKeyboardButtonsStream(buttons).skip(rowsInColumnCount).collect(Collectors.toList());

        for (int i = 0; i < rowsInColumnCount; i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            rowInline.add(firstPart.get(i));
            rowInline.add(secondPart.get(i));
            rowsInline.add(rowInline);
        }

        if (buttons.size() % 2 == 1) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(secondPart.get(rowsInColumnCount));
            rowsInline.add(rowInline);
        }
        return rowsInline;
    }

}
