package home.serg.newsserviceimpl.search.service;

import home.serg.newsserviceimpl.search.dto.PostDto;

import java.util.List;

public interface FeedManager {

    /**
     * @return List of PostDto
     * @see PostDto
     */
    List<PostDto> getPosts();
}
