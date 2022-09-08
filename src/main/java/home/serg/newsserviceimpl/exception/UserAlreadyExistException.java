package home.serg.newsserviceimpl.exception;

/**
 * Exception for duplicate username registration fail
 */
public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
