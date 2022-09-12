package home.serg.newsserviceimpl.rss.service.impl;

import home.serg.newsserviceimpl.exception.NameAlreadyExistException;
import home.serg.newsserviceimpl.exception.NotFoundException;
import home.serg.newsserviceimpl.rss.database.*;
import home.serg.newsserviceimpl.rss.service.RssService;
import home.serg.newsserviceimpl.security.database.UserEntity;
import home.serg.newsserviceimpl.security.database.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RssServiceImpl implements RssService {

    private final UserRepository userRepository;

    private final RssRepository rssRepository;

    private final UserRssRepository userRssRepository;

    @Override
    public List<RssSource> getByUser(User user) {
        UserEntity userEntity = getUserEntity(user);
        return userEntity.getUserRss().stream().map(userRss -> {
            RssSource rss = userRss.getRssSource();
            rss.setIsActive(userRss.getIsActive());
            return rss;
        }).collect(Collectors.toList());
    }

    @Override
    public void addSource(User user, RssSource rssSource) {
        UserEntity userEntity = getUserEntity(user);
        try {
            rssSource.setCreator(userEntity);
            RssSource sourceEntity = rssRepository.save(rssSource);
            UserRss userRss = UserRss.builder().rssSource(sourceEntity).user(userEntity).isActive(true).build();
            if (userEntity.getUserRss() != null) {
                userEntity.getUserRss().add(userRss);
            } else {
                userEntity.setUserRss(Set.of(userRss));
            }
            userRepository.save(userEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new NameAlreadyExistException("Rss title already in use");
        }
    }

    @Override
    public void changeActive(User user, RssSource rssSource) {
        RssSource sourceEntity = getRssEntity(rssSource.getTitle());
        if (Objects.equals(rssSource.getIsActive(), sourceEntity.getIsActive())) return;
        UserEntity userEntity = getUserEntity(user);
        UserRss userRss = userRssRepository.findByUserAndRssSource(userEntity, sourceEntity)
                .orElseThrow(() -> new NotFoundException("Relation not found"));
        userRss.setIsActive(rssSource.getIsActive());
        userRssRepository.save(userRss);
    }

    @Override
    public void delete(User user, String title) {
        UserEntity userEntity = getUserEntity(user);
        RssSource sourceEntity = getRssEntity(title);
        UserRssId id = UserRssId.builder().userId(userEntity.getId()).rssId(sourceEntity.getId()).build();
        try {
            userRssRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Source not found");
        }
    }

    private UserEntity getUserEntity(User user) {
        return userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private RssSource getRssEntity(String title) {
        return rssRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException("Title not found"));
    }
}
