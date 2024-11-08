package com.example.jpa_dynamic_query.service;

import com.example.jpa_dynamic_query.dto.request.LoginRequestDTO;
import com.example.jpa_dynamic_query.dto.request.RegisterRequestDTO;
import com.example.jpa_dynamic_query.dto.response.LoginResponseDTO;
import com.example.jpa_dynamic_query.dto.response.RegisterResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    LoginResponseDTO refreshToken(HttpServletRequest request);

    String registerAccount(RegisterRequestDTO registerRequestDTO);
    String logout(HttpServletRequest request);
}
