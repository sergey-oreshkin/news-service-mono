package home.serg.newsserviceimpl.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@IntegrationTest
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class SecurityTest {

    public static final String VALID_USERNAME = "user";
    public static final String VALID_PASSWORD = "password";
    public static final String INVALID_USERNAME = "invalid";
    public static final String INVALID_PASSWORD = "invalid";
    public static final String CHECK_URL = "/check";

    @Value("${app.jwt.secret}")
    String secretWord;

    @Value("${app.jwt.header}")
    String tokenHeaderName;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    String validAccessToken;
    String expiredAccessToken;
    String invalidUserAccessToken;

    {
        Date now = new Date();
        System.out.println(secretWord);
        validAccessToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 5_000))
                .setSubject(VALID_USERNAME)
                .signWith(SignatureAlgorithm.HS512, secretWord)
                .compact();
        expiredAccessToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() - 5_000))
                .setSubject(VALID_USERNAME)
                .signWith(SignatureAlgorithm.HS512, secretWord)
                .compact();
        invalidUserAccessToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 5_000))
                .setSubject(INVALID_USERNAME)
                .signWith(SignatureAlgorithm.HS512, secretWord)
                .compact();
    }

    @Test
    void check_shouldAnswer200_WhenReceiveValidToken() throws Exception {
        mockMvc.perform(get(CHECK_URL).header(tokenHeaderName, validAccessToken))
                .andExpect(status().isOk());
    }
}
