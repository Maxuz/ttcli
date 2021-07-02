package dev.maxuz.ttcli.exception;

public class TtRuntimeException extends RuntimeException {
    public TtRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TtRuntimeException(String message) {
        super(message);
    }
}
