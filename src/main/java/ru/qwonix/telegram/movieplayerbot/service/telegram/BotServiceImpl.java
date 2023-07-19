package ru.qwonix.telegram.movieplayerbot.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.qwonix.telegram.movieplayerbot.entity.MessageIds;
import ru.qwonix.telegram.movieplayerbot.entity.User;
import ru.qwonix.telegram.movieplayerbot.telegram.bot.TelegramConfig;

@Slf4j
@Service
public class BotServiceImpl implements BotService {

    private final TelegramLongPollingBot bot;
    private final TelegramConfig telegramConfig;

    @Override
    public String getProvidedByText() {
        return "||*Предоставлено @" + telegramConfig.BOT_USERNAME.replaceAll("_", "\\\\_") + "*||";
    }

    public BotServiceImpl(TelegramLongPollingBot bot, TelegramConfig telegramConfig) {
        this.bot = bot;
        this.telegramConfig = telegramConfig;
    }


    private static String escapeMarkdownMessage(String markdownMessage) {
        return markdownMessage
                .replace("-", "\\-")
                .replace("!", "\\!")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("<", "\\<")
                .replace(">", "\\>")
                .replace(".", "\\.");
    }

    @Override
    public void deleteAllMessagesFromUserMessageIds(User user) {
        if (user.getMessageIds().hasSeasonMessageId()) {
            deleteMessage(user, user.getMessageIds().getSeasonMessageId());
        }
        if (user.getMessageIds().hasEpisodeMessageId()) {
            deleteMessage(user, user.getMessageIds().getEpisodeMessageId());
        }
        if (user.getMessageIds().hasVideoMessageId()) {
            deleteMessage(user, user.getMessageIds().getVideoMessageId());
        }
        if (user.getMessageIds().hasSeriesMessageId()) {
            deleteMessage(user, user.getMessageIds().getSeriesMessageId());
        }
    }

    @Override
    public Integer sendVideoWithKeyboard(User user, String fileId, InlineKeyboardMarkup keyboard) {
        return this.sendVideoWithMarkdownTextAndKeyboard(user, null, fileId, keyboard);
    }

    @Override
    public Integer sendVideoWithMarkdownText(User user, String markdownMessage, String fileId) {
        return this.sendVideoWithMarkdownTextAndKeyboard(user, markdownMessage, fileId, null);
    }

    @Override
    public Integer sendVideoWithMarkdownTextAndKeyboard(User user, String markdownMessage, String fileId, InlineKeyboardMarkup keyboard) {
        return this.sendVideo(user
                , SendVideo.builder()
                        .caption(escapeMarkdownMessage(markdownMessage))
                        .parseMode("MarkdownV2")
                        .video(new InputFile(fileId))
                        .replyMarkup(keyboard));
    }

    @Override
    public Integer sendPhotoWithMarkdownText(User user, String markdownMessage, String photoFileId) {
        return this.sendPhotoWithMarkdownTextKeyBoardReply(user, markdownMessage, photoFileId, null, null);
    }

    @Override
    public Integer sendPhotoWithMarkdownTextAndKeyboard(User user, String markdownMessage, String photoFileId, ReplyKeyboard keyboard) {
        return this.sendPhotoWithMarkdownTextKeyBoardReply(user, markdownMessage, photoFileId, keyboard, null);
    }

    @Override
    public Integer sendPhotoWithMarkdownTextKeyBoardReply(User user, String markdownMessage, String photoFileId, ReplyKeyboard keyboard, Integer replayMessageId) {
        return this.sendPhoto(user
                , SendPhoto.builder()
                        .caption(escapeMarkdownMessage(markdownMessage))
                        .parseMode("MarkdownV2")
                        .photo(new InputFile(photoFileId))
                        .replyMarkup(keyboard)
                        .replyToMessageId(replayMessageId));
    }

    @Override
    public Integer sendMarkdownText(User user, String markdownMessage) {
        return this.sendMarkdownTextWithKeyboardAndReplay(user, markdownMessage, null, null);
    }

    @Override
    public Integer sendMarkdownTextWithKeyBoard(User user, String markdownMessage, ReplyKeyboard keyboard) {
        return this.sendMarkdownTextWithKeyboardAndReplay(user, markdownMessage, keyboard, null);
    }

    @Override
    public Integer sendMarkdownTextWithReplay(User user, String markdownMessage, Integer replayMessageId) {
        return this.sendMarkdownTextWithKeyboardAndReplay(user, markdownMessage, null, replayMessageId);
    }

    @Override
    public Integer sendMarkdownTextWithKeyboardAndReplay(User user, String markdownMessage, ReplyKeyboard keyboard, Integer replayMessageId) {
        return this.sendMessage(user
                , SendMessage.builder()
                        .text(escapeMarkdownMessage(markdownMessage))
                        .parseMode("MarkdownV2")
                        .replyMarkup(keyboard)
                        .replyToMessageId(replayMessageId));
    }

    @Override
    public void editPhotoWithMarkdownText(User user, Integer messageId, String markdownMessage, String photoFileId) {
        this.editPhotoWithMarkdownTextAndKeyboard(user, messageId, markdownMessage, photoFileId, null);
    }

    @Override
    public void editPhotoWithKeyboard(User user, int messageId, InlineKeyboardMarkup keyboard, String photoFileId) {
        this.editPhotoWithMarkdownTextAndKeyboard(user, messageId, null, photoFileId, keyboard);
    }

    @Override
    public void editPhotoWithMarkdownTextAndKeyboard(User user, int messageId, String markdownMessage, String photoFileId, InlineKeyboardMarkup keyboard) {
        this.editMedia(user, messageId
                , EditMessageMedia.builder()
                        .media(new InputMediaPhoto(photoFileId)));

        this.editMessageCaption(user, messageId
                , EditMessageCaption.builder()
                        .caption(escapeMarkdownMessage(markdownMessage))
                        .parseMode("MarkdownV2")
                        .replyMarkup(keyboard));
    }

    @Override
    public void editVideoWithMarkdownTextAndKeyboard(User user, Integer messageId, String markdownMessage, String fileId, InlineKeyboardMarkup keyboard) {
        this.editMedia(user, messageId
                , EditMessageMedia.builder()
                        .media(new InputMediaVideo(fileId))
                        .replyMarkup(keyboard));

        this.editMessageCaption(user, messageId
                , EditMessageCaption.builder()
                        .caption(escapeMarkdownMessage(markdownMessage))
                        .parseMode("MarkdownV2")
                        .replyMarkup(keyboard));

    }

    @Override
    public void editVideoWithKeyboard(User user, Integer messageId, String fileId, InlineKeyboardMarkup keyboard) {
        this.editMedia(user, messageId
                , EditMessageMedia.builder()
                        .media(new InputMediaVideo(fileId))
                        .replyMarkup(keyboard));
    }

    @Override
    public void editMarkdownTextWithKeyBoard(User user, int messageId, String markdownMessage, InlineKeyboardMarkup keyboard) {
        this.editMessage(user, messageId
                , EditMessageText.builder()
                        .messageId(messageId)
                        .text(escapeMarkdownMessage(markdownMessage))
                        .parseMode("MarkdownV2")
                        .replyMarkup(keyboard));
    }

    @Override
    public void confirmCallback(String callbackQueryId) {
        this.executeAlertWithText(callbackQueryId, null, null);
    }

    @Override
    public void executeAlertWithText(String callbackQueryId, String text, Boolean showAlert) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .text(text)
                .showAlert(showAlert)
                .build();

        this.executeAlert(answerCallbackQuery);
    }

    @Override
    public void executeAlert(String callbackQueryId, Boolean showAlert) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .showAlert(showAlert)
                .build();

        this.executeAlert(answerCallbackQuery);
    }

    private Integer sendPhoto(User user, SendPhoto.SendPhotoBuilder photoBuilder) {
        SendPhoto photo = photoBuilder
                .chatId(String.valueOf(user.getTelegramId()))
                .disableNotification(true)
                .build();
        try {
            Message execute = bot.execute(photo);
            return execute.getMessageId();
        } catch (TelegramApiException e) {
            log.error("photo sending error " + user, e);
        }
        return null;
    }

    private Integer sendVideo(User user, SendVideo.SendVideoBuilder videoBuilder) {
        SendVideo video = videoBuilder
                .chatId(String.valueOf(user.getTelegramId()))
                .disableNotification(true)
                .build();
        try {
            Message execute = bot.execute(video);
            return execute.getMessageId();
        } catch (TelegramApiException e) {
            log.error("video sending error " + user, e);
        }
        return null;
    }

    private Integer sendMessage(User user, SendMessage.SendMessageBuilder messageBuilder) {
        SendMessage message = messageBuilder.chatId(String.valueOf(user.getTelegramId())).build();
        try {
            Message execute = bot.execute(message);
            return execute.getMessageId();
        } catch (TelegramApiException e) {
            log.error("message sending error " + user, e);
        }
        return null;
    }

    private void editMessage(User user, int messageId, EditMessageText.EditMessageTextBuilder editMessageTextBuilder) {
        EditMessageText editMessage = editMessageTextBuilder
                .chatId(String.valueOf(user.getTelegramId()))
                .messageId(messageId)
                .build();

        try {
            bot.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("message editing error " + user, e);
        }
    }

    private void editMessageCaption(User user, int messageId, EditMessageCaption.EditMessageCaptionBuilder editMessageCaptionBuilder) {
        EditMessageCaption editMessageCaption = editMessageCaptionBuilder
                .chatId(String.valueOf(user.getTelegramId()))
                .messageId(messageId)
                .build();

        try {
            bot.execute(editMessageCaption);
        } catch (TelegramApiException e) {
            log.error("message editing error " + user, e);
        }
    }


    @Override
    public void executeAlert(AnswerCallbackQuery answerCallbackQuery) {
        try {
            bot.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            log.error("execute alert sending error ", e);
        }
    }

    private void editMedia(User user, int messageId, EditMessageMedia.EditMessageMediaBuilder editMessageMediaBuilder) {
        EditMessageMedia editMedia = editMessageMediaBuilder
                .chatId(String.valueOf(user.getTelegramId()))
                .messageId(messageId)
                .build();

        try {
            bot.execute(editMedia);
        } catch (TelegramApiException e) {
            log.error("media editing eror " + user, e);
        }
    }

    @Override
    public Boolean deleteMessage(User user, Integer messageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(String.valueOf(user.getTelegramId()))
                .messageId(messageId).build();
        try {
            return bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error("message deleting error " + user, e);
        }
        return Boolean.FALSE;
    }
}
