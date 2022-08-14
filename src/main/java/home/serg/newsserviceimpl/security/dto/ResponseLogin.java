package home.serg.newsserviceimpl.security.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ResponseLogin {
    String username;
    String token;
    String refresh;
}
