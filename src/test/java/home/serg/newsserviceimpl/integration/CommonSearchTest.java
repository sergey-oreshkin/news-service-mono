package home.serg.newsserviceimpl.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.serg.newsserviceimpl.integration.annotation.IntegrationTest;
import home.serg.newsserviceimpl.rss.database.RssRepository;
import home.serg.newsserviceimpl.rss.database.RssSource;
import home.serg.newsserviceimpl.search.dto.SearchRequestDto;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CommonSearchTest {

    static final String TITLE = "title one";

    static final String LINK = "http://example.com/example1";

    static final String DESCRIPTION = "description example 1 test";

    static final String ResponseXml = format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<rss version=\"2.0\">\n" +
                    "    <channel>\n" +
                    "        <item>\n" +
                    "            <author>example 1</author>\n" +
                    "            <title>%s</title>\n" +
                    "            <link>%s</link>\n" +
                    "            <description>\n" +
                    "                <![CDATA[%s]]>\n" +
                    "            </description>\n" +
                    "            <pubDate>%s</pubDate>\n" +
                    "        </item>\n" +
                    "    </channel>\n" +
                    "</rss>",
            TITLE, LINK, DESCRIPTION, ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RssRepository rssRepository;

    static MockWebServer mockWebServer;

    static List<RssSource> sources;

    @BeforeAll
    static void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        HttpUrl baseUrl = mockWebServer.url("/");
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(ResponseXml)
                .addHeader("Content-Type", "text/xml; charset=UTF-8"));
        sources = List.of(RssSource.builder().link(baseUrl.toString()).build());
    }


    @Test
    void getPosts_shouldFetchPostsFromUrlAndReturnAsJson() throws Exception {

        when(rssRepository.findAll()).thenReturn(sources);

        SearchRequestDto requestDto = new SearchRequestDto(1, List.of(""));

        Thread.sleep(1000);

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].title", is(TITLE)))
                .andExpect(jsonPath("$[0].desc", is(DESCRIPTION)))
                .andExpect(jsonPath("$[0].link", is(LINK)));
    }
}