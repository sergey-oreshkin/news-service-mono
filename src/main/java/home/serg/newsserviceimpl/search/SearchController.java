package home.serg.newsserviceimpl.search;

import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.dto.SearchRequestDto;
import home.serg.newsserviceimpl.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/")
    public List<PostDto> search(@Valid @RequestBody SearchRequestDto request, @AuthenticationPrincipal User user) {
        log.info("Request from {}. Keywords: {}",
                user==null?"unknown":user.getUsername(),
                request.getKeywords().stream().collect(Collectors.joining(", "))
                );
        return user == null
                ?
                searchService.search(request.getHours(), request.getKeywords())
                :
                searchService.search(request.getHours(), request.getKeywords(), user);
    }
}

