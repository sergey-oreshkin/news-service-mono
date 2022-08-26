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

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String secretWord;

    @Value("${app.jwt.tokenTTL}")
    private long tokenTtl;

    @Value("${app.jwt.refreshTTL}")
    private long refreshTtl;

    private final UserRepository userRepository;

    public String getUsernameFromToken(String token) throws JwtException {
        return getClaims(token).getBody().getSubject();
    }

    public TokenType getTypeFromToken(String token) throws JwtException, IllegalArgumentException {
        return TokenType.valueOf(getClaims(token).getBody().get("type", String.class));
    }

    public String getNewAccessToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenTtl * 1000);
        return buildToken(username, now, expiration, TokenType.ACCESS);
    }

    public String getNewRefreshToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTtl * 1000);
        return buildToken(username, now, expiration, TokenType.REFRESH);
    }

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
