package home.serg.newsserviceimpl.rss.repository;

import home.serg.newsserviceimpl.rss.entity.RssSource;
import org.springframework.data.repository.CrudRepository;

public interface RssRepository extends CrudRepository<RssSource, Long> {
    RssSource findByTitle(String title);
}
