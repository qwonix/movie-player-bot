package ru.qwonix.telegram.movieplayerbot.exception;

public class NoSuchSeriesException extends NoSuchCallbackException {
    public NoSuchSeriesException(String message) {
        super(message);
    }
}
