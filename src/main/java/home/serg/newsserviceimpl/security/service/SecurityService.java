package home.serg.newsserviceimpl.security.service;

import home.serg.newsserviceimpl.security.dto.TokenDto;

public interface SecurityService {

    void register(String username, String password);

    TokenDto getLogin(String username, String password);

    TokenDto getRefresh();
}
