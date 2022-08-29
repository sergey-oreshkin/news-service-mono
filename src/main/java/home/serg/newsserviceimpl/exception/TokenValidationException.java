package home.serg.newsserviceimpl.exception;

/**
 * Exception for token validation fail
 */
public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String message) {
        super(message);
    }
}
