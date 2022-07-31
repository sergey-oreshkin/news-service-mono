package home.serg.newsserviceimpl.search.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class PostDto {
    String title;
    String description;
    String link;
    LocalDateTime date;
}
