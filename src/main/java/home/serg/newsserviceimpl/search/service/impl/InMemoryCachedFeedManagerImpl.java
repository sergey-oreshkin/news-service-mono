package home.serg.newsserviceimpl.search.service.impl;

import com.sun.syndication.feed.synd.SyndEntry;
import home.serg.newsserviceimpl.rss.database.RssRepository;
import home.serg.newsserviceimpl.rss.database.RssSource;
import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.entity.Post;
import home.serg.newsserviceimpl.search.service.FeedManager;
import home.serg.newsserviceimpl.search.service.FeedRequester;
import home.serg.newsserviceimpl.search.service.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryCachedFeedManagerImpl implements FeedManager {

    private List<Post> posts;

    private final RssRepository rssRepository;

    private final FeedRequester feedRequester;

    private final PostMapper postMapper;

    @Override
    public List<PostDto> getPosts() {
        return postMapper.toDto(posts);
    }

    @Scheduled(fixedDelayString = "${app.request.period}", initialDelayString = "${app.request.delay}")
    private void refreshNews() {
        List<RssSource> rssSources = (List<RssSource>) rssRepository.findAll();

        log.info("Start refreshing news.");

        posts = (List<Post>) rssSources.parallelStream()
                .map(RssSource::getLink)
                .map(feedRequester::fetchFeedFromUrl)
                .filter(Objects::nonNull)
                .<SyndEntry>flatMap(feed -> feed.getEntries().stream())
                .map(o -> {
                    SyndEntry entry = (SyndEntry) o;
                    return Post.builder()
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
                            .build();
                })
                .collect(Collectors.toList());

        log.info("News refreshed.");
    }
}
