package home.serg.newsserviceimpl.exception;

/**
 * Exception for found username in DB fail
 */
public class UsernameNotFound extends RuntimeException {
    public UsernameNotFound(String message) {
        super(message);
    }
}
