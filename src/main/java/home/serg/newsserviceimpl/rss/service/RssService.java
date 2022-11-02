package home.serg.newsserviceimpl.rss.service;

import home.serg.newsserviceimpl.exception.NameAlreadyExistException;
import home.serg.newsserviceimpl.exception.NotFoundException;
import home.serg.newsserviceimpl.rss.database.RssSource;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface RssService {

    /**
     * Return all rss sources for current user
     *
     * @param user authenticated user
     * @return list of rss sources
     * @throws NotFoundException
     * @see User
     */
    List<RssSource> getByUser(User user);

    /**
     * Add new rss source in db for current user
     *
     * @param user      authenticated user
     * @param rssSource new rss source
     * @throws NameAlreadyExistException
     * @throws NotFoundException
     * @see User
     */
    void addSource(User user, RssSource rssSource);

    /**
     * Change isActive of rss source
     *
     * @param user      authenticated user
     * @param rssSource rss source with new params
     * @throws NotFoundException
     * @see User
     */
    void changeActive(User user, RssSource rssSource);

    /**
     * Delete rss source reference with user
     *
     * @param user  authenticated user
     * @param title of rss source
     * @throws NotFoundException
     * @see User
     */
    void delete(User user, String title);
}
