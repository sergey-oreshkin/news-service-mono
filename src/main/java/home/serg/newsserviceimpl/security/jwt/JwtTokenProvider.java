package home.serg.newsserviceimpl.security.jwt;

import home.serg.newsserviceimpl.exception.TokenValidationException;
import home.serg.newsserviceimpl.security.database.UserEntity;
import home.serg.newsserviceimpl.security.database.UserRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Jwt token processing
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final int MILLIS_IN_SECOND = 1000;

    @Value("${app.jwt.secret}")
    private String secretWord;

    @Value("${app.jwt.tokenTTL}")
    private long tokenTtl;

    @Value("${app.jwt.refreshTTL}")
    private long refreshTtl;

    private final UserRepository userRepository;

    /**
     * Parse token and return subject as username
     *
     * @param token - jwt token
     * @return username
     * @throws JwtException
     */
    public String getUsernameFromToken(String token) throws JwtException {
        return getClaims(token).getBody().getSubject();
    }

    /**
     * Parse token and return type of it
     *
     * @param token - jwt token
     * @return TokenType
     * @throws JwtException
     * @throws IllegalArgumentException
     */
    public TokenType getTypeFromToken(String token) throws JwtException, IllegalArgumentException {
        return TokenType.valueOf(getClaims(token).getBody().get("type", String.class));
    }

    /**
     * Generate new access token
     *
     * @param username - given username
     * @return generated access token
     */
    public String getNewAccessToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenTtl * MILLIS_IN_SECOND);
        return buildToken(username, now, expiration, TokenType.ACCESS);
    }

    /**
     * Generate new refresh token
     *
     * @param username - given username
     * @return generated refresh token
     */
    public String getNewRefreshToken(String username) {
        int randomTimeShift = ThreadLocalRandom.current().nextInt(50);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (refreshTtl + randomTimeShift) * MILLIS_IN_SECOND);
        return buildToken(username, now, expiration, TokenType.REFRESH);
    }

    /**
     * Validate refresh token
     *
     * @param token - given token
     * @throws TokenValidationException  - when type is not refresh or token is not last token
     * @throws UsernameNotFoundException - when user not found in DB
     */
    public void validateRefreshToken(String token) {
        if (Objects.isNull(token) || token.isBlank()) {
            throw new TokenValidationException("Token is empty");
        }
        if (!TokenType.REFRESH.equals(getTypeFromToken(token))) {
            throw new TokenValidationException("Token is not refresh");
        }
        String username = getUsernameFromToken(token);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        String lastToken = user.getLastToken();
        if (!token.equals(lastToken)) {
            user.setLastToken("");
            userRepository.save(user);
            throw new TokenValidationException("Token is expired");
        }
    }

    private String buildToken(String username, Date now, Date expiration, TokenType type) {
        Claims claims = Jwts.claims().setSubject(username);
        return Jwts.builder()
                .setSubject(username)
                .claim("type", type.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, secretWord)
                .compact();
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser().setSigningKey(secretWord).parseClaimsJws(token);
    }
}
