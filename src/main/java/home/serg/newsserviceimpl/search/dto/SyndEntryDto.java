package home.serg.newsserviceimpl.search.dto;

import com.sun.syndication.feed.synd.SyndEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO for fetch rss with source name
 */
@Data
@Builder
@AllArgsConstructor
public class SyndEntryDto {
    List<SyndEntry> entries;
    String sourceName;
}
