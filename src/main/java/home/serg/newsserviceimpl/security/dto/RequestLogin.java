package home.serg.newsserviceimpl.security.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class RequestLogin {

    @NotBlank
    String username;

    @NotBlank
    String password;
}
