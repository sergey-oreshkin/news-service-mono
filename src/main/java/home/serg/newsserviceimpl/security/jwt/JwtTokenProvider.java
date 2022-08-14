package home.serg.newsserviceimpl.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String secretWord;

    @Value("${app.jwt.tokenTTL}")
    private long tokenTtl;

    @Value("${app.jwt.refreshTTL}")
    private long refreshTtl;

    public String getUsernameFromToken(String token) throws JwtException {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretWord).parseClaimsJws(token);
        return claims.getBody().getSubject();
    }

    public String getToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenTtl * 1000);
        return buildToken(username, now, expiration);
    }

    public String getRefresh(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTtl * 1000);
        return buildToken(username, now, expiration);
    }

    private String buildToken(String username, Date now, Date expiration) {
        Claims claims = Jwts.claims().setSubject(username);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, "secret")
                .compact();
    }

    @PostConstruct
    private void ps() {
        System.out.println("--------" + secretWord + "---------------");
    }
}
