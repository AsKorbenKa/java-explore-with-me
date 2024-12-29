package ru.practicum.ewm.exception;

public class DuplicatedDataException extends RuntimeException {
    public DuplicatedDataException(final String message) {
        super(message);
    }
}
