package home.serg.newsserviceimpl.rss.database;

import home.serg.newsserviceimpl.security.database.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRssRepository extends JpaRepository<UserRss, UserRssId> {
    UserRss findByUserAndRssSource(UserEntity user, RssSource source);
}
