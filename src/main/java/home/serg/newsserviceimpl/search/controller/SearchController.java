package home.serg.newsserviceimpl.search.controller;

import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.dto.SearchRequestDto;
import home.serg.newsserviceimpl.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/")
    public List<PostDto> search(@Valid @RequestBody SearchRequestDto request) {

        return searchService.search(request.getHours(), request.getKeywords());
    }
}

