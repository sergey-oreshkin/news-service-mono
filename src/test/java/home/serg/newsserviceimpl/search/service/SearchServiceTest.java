package home.serg.newsserviceimpl.search.service;

import home.serg.newsserviceimpl.search.dto.PostDto;
import home.serg.newsserviceimpl.search.service.impl.SearchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    private static final String TITLE_KEYWORD = "TitLe";
    private static final String DESCRIPTION_KEYWORD = "deSc";
    public static final int MIN_HOURS = 1;
    public static final int MAX_HOURS = 3;


    @Mock
    FeedManager feedManager;

    @InjectMocks
    SearchServiceImpl searchService;

    List<PostDto> posts;

    {
        LocalDateTime now = LocalDateTime.now();
        posts = List.of(
                PostDto.builder()
                        .title(format("Some %s", TITLE_KEYWORD))
                        .description(format("This is %s", DESCRIPTION_KEYWORD))
                        .date(now.minusHours(MIN_HOURS))
                        .build(),
                PostDto.builder()
                        .title(format("Some %s", ""))
                        .description(format("This is %s", ""))
                        .date(now.minusHours(MAX_HOURS))
                        .build(),
                PostDto.builder()
                        .title(format("Some %s", TITLE_KEYWORD))
                        .description(format("This is %s", "empty"))
                        .date(now.minusHours(MAX_HOURS))
                        .build()
        );
    }

    @Test
    void search_shouldReturnEmptyList_WhenFeedManagerReturnNull() {
        List<PostDto> posts = searchService.search(0, Collections.emptyList());

        assertNotNull(posts);
        assertTrue(posts.isEmpty());
    }

    @Test
    void search_shouldReturnEmptyList_WhenFeedManagerReturnEmptyList() {
        when(feedManager.getPosts()).thenReturn(Collections.emptyList());

        List<PostDto> posts = searchService.search(0, Collections.emptyList());

        assertNotNull(posts);
        assertTrue(posts.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getSearchParams")
    void search_shouldReturnPostDependsOnSearchParams(String name, int hours, List<String> keywords, int count) {
        when(feedManager.getPosts()).thenReturn(posts);

        List<PostDto> posts = searchService.search(hours, keywords);

        assertNotNull(posts);
        assertEquals(count, posts.size());
    }

    private static Stream<Arguments> getSearchParams() {
        return Stream.of(
                Arguments.of(
                        "Return all when keywords is empty",
                        5, List.of(""), 3
                ),
                Arguments.of(
                        "Return 1 when hours is MIN",
                        MIN_HOURS + 1, List.of(""), 1
                ),
                Arguments.of(
                        "Return 3 when hours is MAX",
                        MAX_HOURS + 1, List.of(""), 3
                ),
                Arguments.of(
                        "Return 1 by description keyword",
                        MAX_HOURS + 1, List.of(DESCRIPTION_KEYWORD.toLowerCase()), 1
                ),
                Arguments.of(
                        "Return 2 by title keyword",
                        MAX_HOURS + 1, List.of(TITLE_KEYWORD.toLowerCase()), 2
                ),
                Arguments.of(
                        "Return empty list by wrong keyword",
                        MAX_HOURS + 1, List.of("wrong"), 0
                )
        );
    }
}