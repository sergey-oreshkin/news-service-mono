package home.serg.newsserviceimpl.rss.dto;

import lombok.Value;

@Value
public class RssSourceDto {
    String title;
    String link;
    Boolean isActive;
}
