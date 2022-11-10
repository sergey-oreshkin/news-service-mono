package home.serg.newsserviceimpl.search.service.impl;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import home.serg.newsserviceimpl.rss.database.RssSource;
import home.serg.newsserviceimpl.search.dto.SyndEntryDto;
import home.serg.newsserviceimpl.search.service.FeedRequester;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Component
@Slf4j
public class FeedRequesterImpl implements FeedRequester {

    @Value("${app.request.timeout}")
    private int timeout;

    @Override
    public SyndFeed fetchFeedFromUrl(String url) {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(timeout);
            XmlReader reader = new XmlReader(conn);
            return new SyndFeedInput().build(reader);
        } catch (IOException e) {
            log.warn("Failed to connect - " + url + " skipped");
            return null;
        } catch (FeedException | NullPointerException e) {
            log.warn("Failed to parse response from - " + url + " skipped");
            return null;
        }
    }

    @Override
    public SyndEntryDto fetchFeedFromSource(RssSource source) {
        try {
            URLConnection conn = new URL(source.getLink()).openConnection();
            conn.setConnectTimeout(timeout);
            XmlReader reader = new XmlReader(conn);
            return SyndEntryDto.builder()
                    .sourceName(source.getTitle())
                    .entries((List<SyndEntry>) new SyndFeedInput().build(reader).getEntries())
                    .build();
        } catch (IOException e) {
            log.warn("Failed to connect - {} skipped", source.getLink());
            return null;
        } catch (FeedException | NullPointerException e) {
            log.warn("Failed to parse response from - {} skipped", source.getLink());
            return null;
        }
    }
}
