package home.serg.newsserviceimpl.exception;

/**
 * Exception for duplicate username registration fail
 */
public class NameAlreadyExistException extends RuntimeException {
    public NameAlreadyExistException(String message) {
        super(message);
    }
}
