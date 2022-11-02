package home.serg.newsserviceimpl.search.service;

import com.sun.syndication.feed.synd.SyndEntry;
import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.entity.Post;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {
    List<PostDto> toDto(List<Post> posts);

    default List<Post> mapFromEntries(List<SyndEntry> entries) {
        return entries.stream()
                .map(entry -> Post.builder()
                        .title(entry.getTitle().trim().replaceAll("\n|&.{0,};|<.{0,}>", ""))
                        .description(
                                entry.getDescription() == null
                                        ?
                                        ""
                                        :
                                        entry.getDescription().getValue().trim().replaceAll("\n|&.{0,};|<.{0,}>", "")
                        )
                        .link(entry.getLink().trim())
                        .date(
                                entry.getPublishedDate() == null
                                        ?
                                        LocalDateTime.now()
                                        :
                                        entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        )
                        .build()
                ).collect(Collectors.toList());
    }
}
