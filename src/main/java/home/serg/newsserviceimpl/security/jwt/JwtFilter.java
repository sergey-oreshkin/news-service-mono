package home.serg.newsserviceimpl.security.jwt;

import home.serg.newsserviceimpl.exception.TokenValidationException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    @Value("${app.jwt.header")
    String tokenHeaderName;

    private final UserDetailsService userDetailsService;

    private final JwtTokenProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String token = request.getHeader(tokenHeaderName);
        if (Objects.nonNull(token))
            try {
                String username = jwtProvider.getUsernameFromToken(token);
                if ("/refresh".equals(request.getRequestURI())) {
                    jwtProvider.validateRefreshToken(token);
                }
                UserDetails user = userDetailsService.loadUserByUsername(username);
                Authentication auth = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } catch (TokenValidationException ex) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        filterChain.doFilter(request, response);
    }
}
