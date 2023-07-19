package ru.qwonix.telegram.movieplayerbot.service.telegram;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ru.qwonix.telegram.movieplayerbot.entity.MessageIds;
import ru.qwonix.telegram.movieplayerbot.entity.User;

public interface BotService {
    String getProvidedByText();

    void deleteAllMessagesFromUserMessageIds(User user);

    Integer sendVideoWithKeyboard(User user, String fileId, InlineKeyboardMarkup keyboard);

    Integer sendVideoWithMarkdownText(User user, String markdownMessage, String fileId);

    Integer sendVideoWithMarkdownTextAndKeyboard(User user, String markdownMessage, String fileId, InlineKeyboardMarkup keyboard);

    Integer sendPhotoWithMarkdownText(User user, String markdownMessage, String photoFileId);

    Integer sendPhotoWithMarkdownTextAndKeyboard(User user, String markdownMessage, String photoFileId, ReplyKeyboard keyboard);

    Integer sendPhotoWithMarkdownTextKeyBoardReply(User user, String markdownMessage, String photoFileId, ReplyKeyboard keyboard, Integer replayMessageId);

    Integer sendMarkdownText(User user, String markdownMessage);

    Integer sendMarkdownTextWithKeyBoard(User user, String markdownMessage, ReplyKeyboard keyboard);

    Integer sendMarkdownTextWithReplay(User user, String markdownMessage, Integer replayMessageId);

    Integer sendMarkdownTextWithKeyboardAndReplay(User user, String markdownMessage, ReplyKeyboard keyboard, Integer replayMessageId);

    void editPhotoWithMarkdownText(User user, Integer messageId, String markdownMessage, String photoFileId);

    void editPhotoWithKeyboard(User user, int messageId, InlineKeyboardMarkup keyboard, String photoFileId);

    void editPhotoWithMarkdownTextAndKeyboard(User user, int messageId, String markdownMessage, String photoFileId, InlineKeyboardMarkup keyboard);

    void editVideoWithMarkdownTextAndKeyboard(User user, Integer messageId, String markdownMessage, String fileId, InlineKeyboardMarkup keyboard);

    void editVideoWithKeyboard(User user, Integer messageId, String fileId, InlineKeyboardMarkup keyboard);

    void editMarkdownTextWithKeyBoard(User user, int messageId, String markdownMessage, InlineKeyboardMarkup keyboard);

    void confirmCallback(String callbackQueryId);

    void executeAlertWithText(String callbackQueryId, String text, Boolean showAlert);

    void executeAlert(String callbackQueryId, Boolean showAlert);

    void executeAlert(AnswerCallbackQuery answerCallbackQuery);

    Boolean deleteMessage(User user, Integer messageId);
}
