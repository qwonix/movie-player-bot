package ru.qwonix.telegram.movieplayerbot.exception;

public class NoSuchMovieException extends NoSuchCallbackException {
    public NoSuchMovieException(String message) {
        super(message);
    }
}
