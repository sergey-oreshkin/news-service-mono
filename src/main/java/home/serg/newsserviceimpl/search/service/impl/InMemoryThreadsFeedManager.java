package home.serg.newsserviceimpl.search.service.impl;


import com.sun.syndication.feed.synd.SyndEntry;
import home.serg.newsserviceimpl.rss.database.RssRepository;
import home.serg.newsserviceimpl.rss.database.RssSource;
import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.entity.Post;
import home.serg.newsserviceimpl.search.service.FeedManager;
import home.serg.newsserviceimpl.search.service.FeedRequester;
import home.serg.newsserviceimpl.search.service.PostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
public class InMemoryThreadsFeedManager implements FeedManager {
    private List<Post> posts;

    private final PostMapper postMapper;
    private final RssRepository rssRepository;
    private final FeedRequester feedRequester;

    private final ExecutorService executorService;

    @Autowired
    public InMemoryThreadsFeedManager(List<Post> posts, PostMapper postMapper, RssRepository rssRepository,
                                      FeedRequester feedRequester, @Value("${app.request.max-threads}") int maxTreads) {
        this.posts = posts;
        this.postMapper = postMapper;
        this.rssRepository = rssRepository;
        this.feedRequester = feedRequester;
        this.executorService = Executors.newFixedThreadPool(maxTreads);
    }

    @Override
    public List<PostDto> getPosts() {
        return postMapper.toDto(posts);
    }

    @Scheduled(fixedDelayString = "${app.request.period}", initialDelayString = "${app.request.delay}")
    private void refreshNews() {
        List<RssSource> rssSources = (List<RssSource>) rssRepository.findAll();
        log.info("Start refreshing news.");

        List<Future<List<Post>>> futures = rssSources.stream()
                .map(RssSource::getLink)
                .map(link -> new FeedCallable(link, feedRequester, postMapper))
                .map(executorService::submit)
                .collect(Collectors.toList());

        posts = futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                return new ArrayList<Post>();
            }
        }).flatMap(Collection::stream).collect(Collectors.toList());

        log.info("News refreshed.");
    }

    static class FeedCallable implements Callable<List<Post>> {
        private final String link;
        private final FeedRequester feedRequester;
        private final PostMapper postMapper;

        FeedCallable(String link, FeedRequester feedRequester, PostMapper postMapper) {
            this.link = link;
            this.feedRequester = feedRequester;
            this.postMapper = postMapper;
        }

        @Override
        public List<Post> call() throws Exception {
            return postMapper.mapFromEntries((List<SyndEntry>) feedRequester.fetchFeedFromUrl(link).getEntries());
        }
    }

}
