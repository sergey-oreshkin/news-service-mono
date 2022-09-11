package home.serg.newsserviceimpl.rss.database;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RssRepository extends CrudRepository<RssSource, Long> {
    Optional<RssSource> findByTitle(String title);

    List<RssSource> findAllByCreatorUsername(String defaultUsername);
}
