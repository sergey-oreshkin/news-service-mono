package home.serg.newsserviceimpl.search.service;

import home.serg.newsserviceimpl.search.dto.PostDto;

import java.util.List;

public interface SearchService {
    List<PostDto> search(int hours, List<String> keywords);
}
