package home.serg.newsserviceimpl.rss.database;

import home.serg.newsserviceimpl.security.database.UserEntity;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rss_sources")
public class RssSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    private String link;

    @ManyToOne
    @JoinColumn(name = "creator")
    private UserEntity creator;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rssSource", cascade = CascadeType.ALL)
    private Set<UserRss> userRss = new HashSet<>();

    @Transient
    private Boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RssSource rssSource = (RssSource) o;
        return id != null && Objects.equals(id, rssSource.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
