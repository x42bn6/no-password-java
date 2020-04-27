package org.x42bn6.nopassword;

/**
 * A {@code NoPasswordException} is a wrapper parent class for all exceptions thrown by by the application.
 */
public class NoPasswordException extends RuntimeException {
    public NoPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
