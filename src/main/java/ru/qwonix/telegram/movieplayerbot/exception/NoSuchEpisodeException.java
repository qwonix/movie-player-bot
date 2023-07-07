package ru.qwonix.telegram.movieplayerbot.exception;

public class NoSuchEpisodeException extends NoSuchCallbackException {
    public NoSuchEpisodeException(String message) {
        super(message);
    }
}
