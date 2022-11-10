package home.serg.newsserviceimpl.search.service;

import com.sun.syndication.feed.synd.SyndFeed;
import home.serg.newsserviceimpl.rss.database.RssSource;
import home.serg.newsserviceimpl.search.dto.SyndEntryDto;

public interface FeedRequester {

    /**
     * @param url - string value of rss url
     * @return com.sun.syndication.feed.synd.SyndFeed
     * @see com.sun.syndication.feed.synd.SyndFeed
     */
    SyndFeed fetchFeedFromUrl(String url);

    /**
     * @param source - RssSource
     * @return SyndEntryDto
     * @see com.sun.syndication.feed.synd.SyndFeed
     * @see RssSource
     * @see SyndEntryDto
     */
    SyndEntryDto fetchFeedFromSource(RssSource source);

}
