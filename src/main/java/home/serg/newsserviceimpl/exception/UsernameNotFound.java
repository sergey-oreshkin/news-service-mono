package home.serg.newsserviceimpl.exception;

public class UsernameNotFound extends RuntimeException {
    public UsernameNotFound(String message) {
        super(message);
    }
}