package home.serg.newsserviceimpl.security.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenDto {
    String username;
    String token;
    String refresh;
}
