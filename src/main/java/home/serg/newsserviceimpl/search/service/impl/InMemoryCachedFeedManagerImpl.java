package home.serg.newsserviceimpl.search.service.impl;

import com.sun.syndication.feed.synd.SyndEntry;
import home.serg.newsserviceimpl.rss.entity.RssSource;
import home.serg.newsserviceimpl.rss.repository.RssRepository;
import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.entity.Post;
import home.serg.newsserviceimpl.search.service.FeedManager;
import home.serg.newsserviceimpl.search.service.FeedRequester;
import home.serg.newsserviceimpl.search.service.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:default-sources.properties")
public class InMemoryCachedFeedManagerImpl implements FeedManager {

    @Value("#{${sources}}")
    private Map<String, String> sources;

    private List<Post> posts;

    private final RssRepository rssRepository;

    private final FeedRequester feedRequester;

    private final PostMapper postMapper;

    @Override
    public List<PostDto> getPosts() {
        return postMapper.toListDto(posts);
    }

    @PostConstruct
    private void saveDefaultSourcesIntoDb() {
        rssRepository.saveAll(sources.entrySet().stream()
                .map((entry) -> RssSource.builder()
                        .title(entry.getKey())
                        .link(entry.getValue())
                        .isActive(true)
                        .build())
                .map((source) -> rssRepository.findByTitle(source.getTitle()))
                .collect(Collectors.toList()));
    }

    @Scheduled(fixedDelayString = "${app.refresh.period}")
    private void refreshNews() {

        List<RssSource> rssSources = (List<RssSource>) rssRepository.findAll();
        log.info("Start refreshing news.");

        posts = (List<Post>) rssSources.parallelStream()
                .map(RssSource::getLink)
                .map(feedRequester::feedFromUrl)
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
                                            entry.getDescription().getValue().trim().replaceAll("\n|&.{0,};|<.{0,}>", ""
                                            ))
                            .link(entry.getLink().trim())
                            .date(entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .build();
                })
                .collect(Collectors.toList());

        log.info("News refreshed.");
    }
}
