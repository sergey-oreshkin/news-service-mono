package home.serg.newsserviceimpl.rss.database;

import home.serg.newsserviceimpl.security.database.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRssRepository extends JpaRepository<UserRss, UserRssId> {
    Optional<UserRss> findByUserAndRssSource(UserEntity user, RssSource source);
}
