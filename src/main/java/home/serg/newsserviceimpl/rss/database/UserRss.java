package home.serg.newsserviceimpl.rss.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import home.serg.newsserviceimpl.security.database.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users_rss_sources")
public class UserRss {

    @EmbeddedId
    private UserRssId userRssId = new UserRssId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserEntity user;

    @ManyToOne
    @MapsId("rssId")
    @JoinColumn(name = "rss_id")
    @JsonIgnore
    private RssSource rssSource;

    private Boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof UserRss) {
            UserRss that = (UserRss) o;
            return Objects.equals(user, that.getUser()) && Objects.equals(rssSource, that.getRssSource());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
