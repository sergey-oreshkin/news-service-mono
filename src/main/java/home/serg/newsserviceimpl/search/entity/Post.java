package home.serg.newsserviceimpl.search.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Post {
    private Long id;
    private String sourceName;
    private String title;
    private String description;
    private String link;
    private LocalDateTime date;
}
