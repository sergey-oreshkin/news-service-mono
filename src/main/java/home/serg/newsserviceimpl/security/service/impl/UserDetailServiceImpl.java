package home.serg.newsserviceimpl.security.service.impl;

import home.serg.newsserviceimpl.security.database.UserEntity;
import home.serg.newsserviceimpl.security.database.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * @param username - given username
     * @return UserDetails for Spring Security
     * @throws UsernameNotFoundException - when user not found in DB
     * @see UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}
