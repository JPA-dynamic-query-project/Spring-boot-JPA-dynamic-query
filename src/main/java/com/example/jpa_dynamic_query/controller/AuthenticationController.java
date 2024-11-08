package com.example.jpa_dynamic_query.controller;

import com.example.jpa_dynamic_query.dto.request.LoginRequestDTO;
import com.example.jpa_dynamic_query.dto.request.RegisterRequestDTO;
import com.example.jpa_dynamic_query.dto.request.SearchCarRequestDTO;
import com.example.jpa_dynamic_query.exception.AppException;
import com.example.jpa_dynamic_query.response.DataResponse;
import com.example.jpa_dynamic_query.response.ErrorResponse;
import com.example.jpa_dynamic_query.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO
    ){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(
                            HttpStatus.OK.value(),
                            "Login successfully",
                            authenticationService.login(loginRequestDTO)
                    )
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request
    ){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(
                            HttpStatus.OK.value(),
                            "Refresh successfully",
                            authenticationService.refreshToken(request)
                    )
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerAccount(
            @Valid @RequestBody RegisterRequestDTO registerRequestDTO
    ){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(
                            HttpStatus.OK.value(),
                            authenticationService.registerAccount(registerRequestDTO)
                    )
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request
    ){
        try {
            return ResponseEntity.ok().body(
                    new DataResponse<>(
                            HttpStatus.OK.value(),
                            authenticationService.logout(request)
                    )
            );
        }catch (AppException e){
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
}
