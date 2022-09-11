package home.serg.newsserviceimpl.exception;

import home.serg.newsserviceimpl.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for all application layer
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NameAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserAlreadyExistException(NameAlreadyExistException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(TokenValidationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAuthenticationException(TokenValidationException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}
