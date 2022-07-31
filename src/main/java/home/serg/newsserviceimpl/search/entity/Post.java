package home.serg.newsserviceimpl.search.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Post {
    Long id;
    String sourceName;
    String title;
    String description;
    String link;
    LocalDateTime date;
}
