package home.serg.newsserviceimpl.rss.service.impl;

import home.serg.newsserviceimpl.exception.NameAlreadyExistException;
import home.serg.newsserviceimpl.exception.NotFoundException;
import home.serg.newsserviceimpl.rss.database.RssRepository;
import home.serg.newsserviceimpl.rss.database.RssSource;
import home.serg.newsserviceimpl.rss.database.UserRss;
import home.serg.newsserviceimpl.rss.database.UserRssRepository;
import home.serg.newsserviceimpl.security.database.UserEntity;
import home.serg.newsserviceimpl.security.database.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RssServiceImplTest {

    public static final String DEFAULT_USERNAME = "user";

    public static final String DEFAULT_RSS_TITLE = "test rss";

    @Mock
    UserRepository userRepository;

    @Mock
    RssRepository rssRepository;

    @Mock
    UserRssRepository userRssRepository;

    @InjectMocks
    RssServiceImpl rssService;

    @Test
    void getByUser_shouldReturnListOfRssSources_WhenUserInDbAndUserHaveRssSources() {
        UserRss userRss = UserRss.builder().rssSource(RssSource.builder().title(DEFAULT_RSS_TITLE).build()).isActive(false).build();
        UserEntity userEntity = UserEntity.builder().userRss(Set.of(userRss)).username(DEFAULT_USERNAME).build();
        User user = (User) User.withUsername(DEFAULT_USERNAME).password("").authorities("NONE").build();

        when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(userEntity));

        List<RssSource> sources = rssService.getByUser(user);

        assertFalse(sources.isEmpty());
        assertEquals(DEFAULT_RSS_TITLE, sources.get(0).getTitle());
    }

    @Test
    void addSource_shouldThrowNameAlreadyExistException_WhenRssNameIsInDb() {
        RssSource source = RssSource.builder().title(DEFAULT_RSS_TITLE).build();
        UserEntity userEntity = UserEntity.builder().username(DEFAULT_USERNAME).build();
        User user = (User) User.withUsername(DEFAULT_USERNAME).password("").authorities("NONE").build();

        when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(userEntity));
        when(rssRepository.save(source)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(NameAlreadyExistException.class, () -> rssService.addSource(user, source));
    }

    @Test
    void addSource_shouldCallRssRepositorySaveMethodAndUserRepositorySaveMethod() {
        RssSource source = RssSource.builder().title(DEFAULT_RSS_TITLE).build();
        UserEntity userEntity = UserEntity.builder().username(DEFAULT_USERNAME).build();
        User user = (User) User.withUsername(DEFAULT_USERNAME).password("").authorities("NONE").build();

        when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(userEntity));
        when(rssRepository.save(source)).thenReturn(source);

        rssService.addSource(user, source);

        verify(rssRepository, times(1)).save(source);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void changeActive_shouldCallUserRssRepositorySave_WhenIsActiveIsChanged() {
        UserRss userRss = UserRss.builder().rssSource(RssSource.builder().title(DEFAULT_RSS_TITLE).build()).isActive(false).build();
        UserEntity userEntity = UserEntity.builder().userRss(Set.of(userRss)).username(DEFAULT_USERNAME).build();
        User user = (User) User.withUsername(DEFAULT_USERNAME).password("").authorities("NONE").build();
        RssSource source = RssSource.builder().title(DEFAULT_RSS_TITLE).isActive(false).build();

        when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(userEntity));
        when(rssRepository.findByTitle(DEFAULT_RSS_TITLE)).thenReturn(Optional.of(source));
        when(userRssRepository.findByUserAndRssSource(userEntity, source)).thenReturn(Optional.of(userRss));

        RssSource newRssSource = RssSource.builder().title(DEFAULT_RSS_TITLE).isActive(true).build();

        rssService.changeActive(user, newRssSource);

        verify(userRssRepository, times(1)).save(userRss);
    }

    @Test
    void delete_shouldThrowNotFoundException_WhenRepoThrowEmptyResultDataAccessException() {
        User user = (User) User.withUsername(DEFAULT_USERNAME).password("").authorities("NONE").build();

        doThrow(new EmptyResultDataAccessException(1)).when(userRssRepository).deleteById(any());

        assertThrows(NotFoundException.class, () -> rssService.delete(user, DEFAULT_RSS_TITLE));
    }

    @Test
    void delete_shouldCallUserRssRepositoryDeleteById() {
        UserEntity userEntity = UserEntity.builder().username(DEFAULT_USERNAME).build();
        User user = (User) User.withUsername(DEFAULT_USERNAME).password("").authorities("NONE").build();
        RssSource source = RssSource.builder().title(DEFAULT_RSS_TITLE).isActive(false).build();

        when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(userEntity));
        when(rssRepository.findByTitle(DEFAULT_RSS_TITLE)).thenReturn(Optional.of(source));

        rssService.delete(user, DEFAULT_RSS_TITLE);

        verify(userRssRepository, times(1)).deleteById(any());
    }
}