package home.serg.newsserviceimpl.security.service.impl;

import home.serg.newsserviceimpl.exception.NameAlreadyExistException;
import home.serg.newsserviceimpl.rss.database.RssRepository;
import home.serg.newsserviceimpl.rss.database.RssSource;
import home.serg.newsserviceimpl.rss.database.UserRss;
import home.serg.newsserviceimpl.security.database.UserEntity;
import home.serg.newsserviceimpl.security.database.UserRepository;
import home.serg.newsserviceimpl.security.dto.TokenDto;
import home.serg.newsserviceimpl.security.jwt.JwtTokenProvider;
import home.serg.newsserviceimpl.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;

    private final RssRepository rssRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtProvider;

    @Override
    public void register(String username, String password) {
        try {
            UserEntity user = userRepository.save(UserEntity.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .build());
            setDefaultRss(user);
        } catch (DataIntegrityViolationException e) {
            throw new NameAlreadyExistException("Username already in use");
        }
    }

    @Override
    public TokenDto getRefresh() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return getResponseLoginAndUpdateRefreshToken(username);
    }

    @Override
    public TokenDto getLogin(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return getResponseLoginAndUpdateRefreshToken(username);
    }

    private TokenDto getResponseLoginAndUpdateRefreshToken(String username) {
        String refreshToken = jwtProvider.getNewRefreshToken(username);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("%s not found", username)));
        user.setLastToken(refreshToken);
        userRepository.save(user);
        return TokenDto.builder()
                .username(username)
                .token(jwtProvider.getNewAccessToken(username))
                .refresh(refreshToken)
                .build();
    }

    private void setDefaultRss(UserEntity user) {
        List<RssSource> rssSources = rssRepository.findAllByCreatorUsername("default");
        Set<UserRss> userRss = rssSources.stream()
                .map(rss -> UserRss.builder().rssSource(rss).user(user).isActive(true).build())
                .collect(Collectors.toSet());
        user.setUserRss(userRss);
        userRepository.save(user);
    }
}
