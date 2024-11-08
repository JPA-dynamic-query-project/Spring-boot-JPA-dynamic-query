package com.example.jpa_dynamic_query.dto.request;

import com.example.jpa_dynamic_query.entity.Role;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RegisterRequestDTO {
    @NotBlank(message = "username must be not blank")
    private String username;
    @NotBlank(message = "email must be not blank")
    @Email
    private String email;
    @NotBlank(message = "password must be not blank")
    private String password;
    private Set<String> roles;
}
