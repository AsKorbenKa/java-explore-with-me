package ru.practicum.ewm.exception;

public class ConditionsNotMetException extends RuntimeException {
    public ConditionsNotMetException(final String message) {
        super(message);
    }
}
