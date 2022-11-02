package home.serg.newsserviceimpl.security.service;

import home.serg.newsserviceimpl.exception.NameAlreadyExistException;
import home.serg.newsserviceimpl.security.dto.TokenDto;

public interface SecurityService {

    /**
     * Save new user in DB with given password
     *
     * @param username - Name of the user
     * @param password - Password of the user
     * @throws NameAlreadyExistException
     * @see NameAlreadyExistException
     */
    void register(String username, String password);

    /**
     * Authenticate user and return new tokens as TokenDto
     *
     * @param username - Name of the user
     * @param password - Password of the user
     * @return TokenDto with new tokens
     * @see TokenDto
     */
    TokenDto getLogin(String username, String password);

    /**
     * Return new tokens as TokenDto
     *
     * @return TokenDto with new tokens
     * @see TokenDto
     */
    TokenDto getRefresh();
}
