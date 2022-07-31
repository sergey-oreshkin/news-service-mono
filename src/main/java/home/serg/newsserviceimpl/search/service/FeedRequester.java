package home.serg.newsserviceimpl.search.service;

import com.sun.syndication.feed.synd.SyndFeed;

public interface FeedRequester {

    SyndFeed feedFromUrl(String url);
}
