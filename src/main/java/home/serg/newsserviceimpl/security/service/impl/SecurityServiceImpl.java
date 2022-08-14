package home.serg.newsserviceimpl.security.service.impl;

import home.serg.newsserviceimpl.exception.UserAlreadyExistException;
import home.serg.newsserviceimpl.security.dto.ResponseLogin;
import home.serg.newsserviceimpl.security.entity.UserEntity;
import home.serg.newsserviceimpl.security.jwt.JwtTokenProvider;
import home.serg.newsserviceimpl.security.repository.UserRepository;
import home.serg.newsserviceimpl.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseLogin getRefresh() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return getResponseLogin(username);
    }

    @Override
    public ResponseLogin getLogin(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return getResponseLogin(username);
    }

    private ResponseLogin getResponseLogin(String username) {
        return ResponseLogin.builder()
                .username(username)
                .token(jwtProvider.getToken(username))
                .refresh(jwtProvider.getRefresh(username))
                .build();
    }
}
