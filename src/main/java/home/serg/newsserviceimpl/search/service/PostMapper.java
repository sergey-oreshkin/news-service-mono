package home.serg.newsserviceimpl.search.service;

import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.entity.Post;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    List<PostDto> toListDto(List<Post> posts);
}
