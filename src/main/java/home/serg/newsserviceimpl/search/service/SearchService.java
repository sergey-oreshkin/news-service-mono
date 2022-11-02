package home.serg.newsserviceimpl.search.service;

import home.serg.newsserviceimpl.search.dto.PostDto;

import java.util.List;

public interface SearchService {

    /**
     * @param hours    - hours ago
     * @param keywords - keywords contains
     * @return List of PostDto contains given keywords and for last given hours
     * @see PostDto
     */
    List<PostDto> search(int hours, List<String> keywords);
}
