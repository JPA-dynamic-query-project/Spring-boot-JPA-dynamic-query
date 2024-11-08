package com.example.jpa_dynamic_query.service.Impl;

import com.example.jpa_dynamic_query.auth.detail.UserDetailsImpl;
import com.example.jpa_dynamic_query.auth.detail.UserDetailsServiceImpl;
import com.example.jpa_dynamic_query.auth.utils.JwtUtils;
import com.example.jpa_dynamic_query.dto.request.LoginRequestDTO;
import com.example.jpa_dynamic_query.dto.request.RegisterRequestDTO;
import com.example.jpa_dynamic_query.dto.response.LoginResponseDTO;
import com.example.jpa_dynamic_query.dto.response.RegisterResponseDTO;
import com.example.jpa_dynamic_query.entity.Role;
import com.example.jpa_dynamic_query.entity.Token;
import com.example.jpa_dynamic_query.entity.User;
import com.example.jpa_dynamic_query.enums.EnumRole;
import com.example.jpa_dynamic_query.enums.TokenType;
import com.example.jpa_dynamic_query.exception.AppException;
import com.example.jpa_dynamic_query.repository.RoleRepository;
import com.example.jpa_dynamic_query.repository.UserRepository;
import com.example.jpa_dynamic_query.service.AuthenticationService;
import com.example.jpa_dynamic_query.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.REFERER;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);;
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority item : user.getAuthorities()) {
            String authority = item.getAuthority();
            roles.add(authority);
        }
        tokenService.save(
                Token.builder()
                        .username(user.getUsername())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build()
        );
        return LoginResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(roles)
                .authenticated(true)
                .build();
    }

    @Override
    public LoginResponseDTO refreshToken(HttpServletRequest request) {
        final String refreshToken = request.getHeader(REFERER);
        if (StringUtils.isBlank(refreshToken)){
            throw new AppException("Refresh token is not blank");
        }

        final String username = jwtUtils.getUserNameFromJwtToken(refreshToken, TokenType.REFRESH_TOKEN);
        UserDetailsImpl user = userDetailsService.loadUserByUsername(username);

        if (!jwtUtils.validateJwtToken(refreshToken, TokenType.REFRESH_TOKEN, user)){
            throw new AppException("Not allow access with this token");
        }

        String accessToken = jwtUtils.generateAccessToken(user);
        tokenService.save(
                Token.builder()
                        .username(user.getUsername())
                        .refreshToken(refreshToken)
                        .accessToken(accessToken)
                        .build()
        );
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority item : user.getAuthorities()) {
            String authority = item.getAuthority();
            roles.add(authority);
        }
        return LoginResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(roles)
                .authenticated(true)
                .build();
    }

    @Override
    public String registerAccount(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())){
            throw new AppException("email is existed");
        }
        if (userRepository.existsByUsername(registerRequestDTO.getUsername())){
            throw new AppException("username is existed");
        }

        User user = new User(registerRequestDTO.getUsername(),
                            registerRequestDTO.getEmail(),
                            passwordEncoder.encode(registerRequestDTO.getPassword()));

        Set<String> strRoles = registerRequestDTO.getRoles();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null){
            Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
                    .orElseThrow(()-> new AppException("Error: Role is not found"));
            roles.add(userRole);
        }else {
            for (String role : strRoles) {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(EnumRole.ROLE_ADMIN)
                                .orElseThrow(() -> new AppException("Error: Role is not found"));
                        roles.add(adminRole);
                        break;
                    case "user":
                        Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
                                .orElseThrow(() -> new AppException("Error: Role is not found"));
                        roles.add(userRole);
                        break;
                    default:
                        break;
                }
            }
        }
        user.setRoles(roles);
        userRepository.save(user);
        return "Register successfully";
    }

    @Override
        public String logout(HttpServletRequest request) {

        final String token = request.getHeader(REFERER);
        if (StringUtils.isBlank(token)) {
            throw new AppException("Token must be not blank");
        }

        final String userName = jwtUtils.getUserNameFromJwtToken(token, TokenType.REFRESH_TOKEN);
        tokenService.delete(userName);

        return "Logout successfully";
    }
}
