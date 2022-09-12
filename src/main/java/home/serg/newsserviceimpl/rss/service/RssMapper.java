package home.serg.newsserviceimpl.rss.service;

import home.serg.newsserviceimpl.rss.database.RssSource;
import home.serg.newsserviceimpl.rss.dto.RssSourceDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RssMapper {

    List<RssSourceDto> toDto(List<RssSource> rssSources);

    RssSource toEntity(RssSourceDto sourceDto);
}
