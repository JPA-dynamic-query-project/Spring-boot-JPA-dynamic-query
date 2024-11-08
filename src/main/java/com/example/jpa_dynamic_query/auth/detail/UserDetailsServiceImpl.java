package com.example.jpa_dynamic_query.auth.detail;

import com.example.jpa_dynamic_query.auth.detail.UserDetailsImpl;
import com.example.jpa_dynamic_query.entity.User;
import com.example.jpa_dynamic_query.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

//return UserDetails object to Spring security can use for authentication and validation
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    @Transactional
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(
                        role -> new SimpleGrantedAuthority(role.getName().name()))
                        .collect(Collectors.toList())
                )
                .build();
    }
}
