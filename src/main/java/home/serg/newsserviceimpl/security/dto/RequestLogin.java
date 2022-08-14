package home.serg.newsserviceimpl.security.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class RequestLogin {

    @NotBlank
    String username;

    @NotBlank
    String password;
}
