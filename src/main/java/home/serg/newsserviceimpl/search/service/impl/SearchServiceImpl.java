package home.serg.newsserviceimpl.search.service.impl;

import home.serg.newsserviceimpl.rss.database.RssSource;
import home.serg.newsserviceimpl.rss.service.RssService;
import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.service.FeedManager;
import home.serg.newsserviceimpl.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final FeedManager feedManager;

    private final RssService rssService;

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
                    .sorted(Comparator.comparing(PostDto::getDate).reversed())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<PostDto> search(int hours, List<String> keywords, User user) {
        Set<String> sources = rssService.getByUser(user).stream()
                .filter(RssSource::getIsActive)
                .map(RssSource::getTitle)
                .collect(Collectors.toSet());
        return search(hours, keywords).stream()
                .filter(postDto -> sources.contains(postDto.getSourceName()))
                .collect(Collectors.toList());
    }
}
