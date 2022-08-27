package home.serg.newsserviceimpl.search.service.impl;

import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.service.FeedManager;
import home.serg.newsserviceimpl.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final FeedManager feedManager;

    @Override
    public List<PostDto> search(int hours, List<String> keywords) {
        LocalDateTime earliestDate = LocalDateTime.now().minusHours(hours);
        List<PostDto> posts = feedManager.getPosts();
        if (Objects.nonNull(posts)) {
            return posts.stream()
                    .filter(p -> p.getDate().isAfter(earliestDate))
                    .filter(p -> {
                        for (String s : keywords) {
                            if (p.getTitle().trim().toLowerCase().contains(s.toLowerCase()) ||
                                    p.getDescription().trim().toLowerCase().contains(s.toLowerCase())) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
