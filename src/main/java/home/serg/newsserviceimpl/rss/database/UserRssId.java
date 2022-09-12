package home.serg.newsserviceimpl.rss.database;


import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class UserRssId implements Serializable {

    private Long userId;

    private Long rssId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRssId userRssId = (UserRssId) o;
        return Objects.equals(userId, userRssId.userId) && Objects.equals(rssId, userRssId.rssId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, rssId);
    }
}
