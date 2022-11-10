package home.serg.newsserviceimpl.search.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for post of news
 */
@Value
@Builder
public class PostDto {

    String title;

    @JsonProperty("desc")
    String description;

    String link;

    LocalDateTime date;

    @JsonIgnore
    String sourceName;
}
