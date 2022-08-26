package home.serg.newsserviceimpl.security.service.impl;

import home.serg.newsserviceimpl.exception.UserAlreadyExistException;
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

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtProvider;

    @Override
    public void register(String username, String password) {
        try {
            userRepository.save(UserEntity.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .build());
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException("Username already in use");
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
                .refresh(jwtProvider.getNewRefreshToken(username))
                .build();
    }
}
