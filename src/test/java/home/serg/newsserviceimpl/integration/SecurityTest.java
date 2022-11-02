package home.serg.newsserviceimpl.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.serg.newsserviceimpl.integration.annotation.IntegrationTest;
import home.serg.newsserviceimpl.security.database.UserEntity;
import home.serg.newsserviceimpl.security.database.UserRepository;
import home.serg.newsserviceimpl.security.dto.RequestLogin;
import home.serg.newsserviceimpl.security.dto.TokenDto;
import home.serg.newsserviceimpl.security.jwt.TokenType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecurityTest {

    public static final String VALID_USERNAME = "user";
    public static final String REGISTER_USERNAME = "register";
    public static final String VALID_PASSWORD = "password";
    public static final String INVALID_USERNAME = "invalid";
    public static final String INVALID_PASSWORD = "invalid";
    public static final String CHECK_URL = "/check";
    public static final String REGISTER_URL = "/register";
    public static final String LOGIN_URL = "/login";
    public static final String REFRESH_URL = "/refresh";

    public static final int TIME_INTERVAL_MILLIS = 50_000;

    @Value("${app.jwt.secret}")
    String secretWord;

    @Value("${app.jwt.header}")
    String tokenHeaderName;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeAll
    void init() {
        System.out.println("****************");
        List<UserEntity> all = (List<UserEntity>) userRepository.findAll();
        all.forEach(System.out::println);
        final UserEntity validUser = UserEntity.builder()
                .username(VALID_USERNAME)
                .password(passwordEncoder.encode(VALID_PASSWORD))
                .build();
        userRepository.save(validUser);

    }

    @Test
    void check_shouldAnswer200_WhenReceiveValidToken() throws Exception {
        mockMvc.perform(get(CHECK_URL)
                        .header(tokenHeaderName, getToken(VALID_USERNAME, TIME_INTERVAL_MILLIS, TokenType.ACCESS)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badTokenSource")
    void check_shouldAnswer403_WhenTokenIsInvalid(String name, String token) throws Exception {
        mockMvc.perform(get(CHECK_URL).header(tokenHeaderName, token))
                .andExpect(status().isForbidden());
    }

    private Stream<Arguments> badTokenSource() {
        return Stream.of(
                Arguments.of("Invalid username", getToken(INVALID_USERNAME, TIME_INTERVAL_MILLIS, TokenType.ACCESS)),
                Arguments.of("Expired token", getToken(VALID_USERNAME, -TIME_INTERVAL_MILLIS, TokenType.ACCESS)),
                Arguments.of("Refresh type", getToken(VALID_USERNAME, TIME_INTERVAL_MILLIS, TokenType.REFRESH))
        );
    }

    @Test
    void register_shouldAnswer201_WhenRegisterIsSuccess() throws Exception {
        RequestLogin loginDto = RequestLogin.builder().username(REGISTER_USERNAME).password(VALID_PASSWORD).build();

        mockMvc.perform(post(REGISTER_URL).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_shouldAnswer400_WhenUsernameAlreadyInUse() throws Exception {
        RequestLogin loginDto = RequestLogin.builder().username(VALID_USERNAME).password(VALID_PASSWORD).build();

        mockMvc.perform(post(REGISTER_URL).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldAnswer200AndReturnTokens_WhenUsernameAndPasswordIsCorrect() throws Exception {
        RequestLogin loginDto = RequestLogin.builder().username(VALID_USERNAME).password(VALID_PASSWORD).build();

        mockMvc.perform(post(LOGIN_URL).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(VALID_USERNAME)))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.refresh", notNullValue()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badLoginDto")
    void login_shouldAnswer403_WhenBadCredentials(String name, RequestLogin badLogin) throws Exception {
        mockMvc.perform(post(LOGIN_URL).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized());
    }

    private Stream<Arguments> badLoginDto() {
        return Stream.of(
                Arguments.of("Wrong username", RequestLogin.builder().username(INVALID_USERNAME).password(VALID_PASSWORD).build()),
                Arguments.of("Wrong password", RequestLogin.builder().username(VALID_USERNAME).password(INVALID_PASSWORD).build())
        );
    }

    @Test
    void refresh_shouldAnswer200AndReturnTokens_WhenRefreshTokenIsValid() throws Exception {
        RequestLogin loginDto = RequestLogin.builder().username(VALID_USERNAME).password(VALID_PASSWORD).build();
        String json = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andReturn().getResponse().getContentAsString();
        TokenDto tokenDto = mapper.readValue(json, TokenDto.class);

        mockMvc.perform(get(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(tokenHeaderName, tokenDto.getRefresh()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(VALID_USERNAME)))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.refresh", notNullValue()));
    }

    @Test
    void refresh_shouldAnswer403_whenTokenTypeIsAccess() throws Exception {
        RequestLogin loginDto = RequestLogin.builder().username(VALID_USERNAME).password(VALID_PASSWORD).build();
        String json = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andReturn().getResponse().getContentAsString();
        TokenDto tokenDto = mapper.readValue(json, TokenDto.class);

        mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(tokenHeaderName, tokenDto.getToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void refresh_shouldAnswer403_whenTokenSendTwice() throws Exception {
        RequestLogin loginDto = RequestLogin.builder().username(VALID_USERNAME).password(VALID_PASSWORD).build();
        String json = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andReturn().getResponse().getContentAsString();
        TokenDto tokenDto = mapper.readValue(json, TokenDto.class);

        mockMvc.perform(get(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(tokenHeaderName, tokenDto.getRefresh()))
                .andExpect(status().isOk());

        mockMvc.perform(get(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(tokenHeaderName, tokenDto.getRefresh()))
                .andExpect(status().isForbidden());
    }

    private String getToken(String username, int ttl, TokenType type) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .claim("type", type.toString())
                .setExpiration(new Date(now.getTime() + ttl))
                .signWith(SignatureAlgorithm.HS512, secretWord)
                .compact();
    }
}
