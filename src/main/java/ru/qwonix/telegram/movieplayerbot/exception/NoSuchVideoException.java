package ru.qwonix.telegram.movieplayerbot.exception;

public class NoSuchVideoException extends NoSuchCallbackException {
    public NoSuchVideoException(String message) {
        super(message);
    }
}
