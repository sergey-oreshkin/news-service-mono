package home.serg.newsserviceimpl.search.service;

import com.sun.syndication.feed.synd.SyndFeed;

public interface FeedRequester {

    /**
     * @param url - string value of rss url
     * @return com.sun.syndication.feed.synd.SyndFeed
     * @See com.sun.syndication.feed.synd.SyndFeed
     */
    SyndFeed fetchFeedFromUrl(String url);
}
