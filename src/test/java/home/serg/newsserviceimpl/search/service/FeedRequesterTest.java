package home.serg.newsserviceimpl.search.service;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import home.serg.newsserviceimpl.search.service.impl.FeedRequesterImpl;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FeedRequesterTest {

    static final FeedRequester feedRequester = new FeedRequesterImpl();

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
                    "            <pubDate>{date1}</pubDate>\n" +
                    "        </item>\n" +
                    "        <item>\n" +
                    "            <author>example 2</author>\n" +
                    "            <title>title two</title>\n" +
                    "            <link>http://example.com/example2</link>\n" +
                    "            <description>\n" +
                    "                <![CDATA[description example 2 test]]>\n" +
                    "            </description>\n" +
                    "            <pubDate>{date2}</pubDate>\n" +
                    "        </item>\n" +
                    "        <item>\n" +
                    "            <author>example 3</author>\n" +
                    "            <title>title three </title>\n" +
                    "            <link>http://example.com/example3</link>\n" +
                    "            <description>\n" +
                    "                <![CDATA[description example 3 test]]>\n" +
                    "            </description>\n" +
                    "            <pubDate>{date3}</pubDate>\n" +
                    "        </item>\n" +
                    "        <item>\n" +
                    "            <author>example 4</author>\n" +
                    "            <title>title four</title>\n" +
                    "            <link>http://example.com/example4</link>\n" +
                    "            <description>\n" +
                    "                <![CDATA[description example 4 test]]>\n" +
                    "            </description>\n" +
                    "            <pubDate>{date4}</pubDate>\n" +
                    "        </item>\n" +
                    "    </channel>\n" +
                    "</rss>",
            TITLE, LINK, DESCRIPTION);

    @Test
    void fetchFeedFromUrl_shouldReturnParsedFeed() throws IOException {
        SyndFeed feed;
        SyndEntry entry;

        try (MockWebServer mockWebServer = new MockWebServer()) {
            mockWebServer.start();
            HttpUrl baseUrl = mockWebServer.url("/");
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody(ResponseXml)
                    .addHeader("Content-Type", "text/xml; charset=UTF-8"));

            feed = feedRequester.fetchFeedFromUrl(baseUrl.toString());
            entry = (SyndEntry) feed.getEntries().get(0);
        }

        assertNotNull(feed, "feed is null");
        assertEquals(4, feed.getEntries().size(), "wrong entries count");

        assertEquals(TITLE, entry.getTitle(), "wrong title");
        assertEquals(LINK, entry.getLink(), "wrong link");
        assertEquals(DESCRIPTION, entry.getDescription().getValue().trim(), "wrong description");
    }
}