package home.serg.newsserviceimpl.security.service;

import home.serg.newsserviceimpl.security.dto.ResponseLogin;

public interface SecurityService {

    void register(String username, String password);

    ResponseLogin getLogin(String username, String password);

    ResponseLogin getRefresh();
}
