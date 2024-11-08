package com.example.jpa_dynamic_query.dto.response;

import com.example.jpa_dynamic_query.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Builder
@Getter
public class LoginResponseDTO {
    private Integer id;
    private String email;
    private String username;
    private String accessToken;
    private String refreshToken;
    private List<String> roles;
    private boolean authenticated;
}
