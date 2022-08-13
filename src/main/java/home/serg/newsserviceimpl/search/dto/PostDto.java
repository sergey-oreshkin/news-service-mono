package home.serg.newsserviceimpl.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class PostDto {

    String title;

    @JsonProperty("desc")
    String description;

    String link;

    LocalDateTime date;
}
