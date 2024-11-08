package com.example.jpa_dynamic_query.service;

import com.example.jpa_dynamic_query.entity.Token;

public interface TokenService {
    Token getTokenByUsername(String username);
    int save(Token token);
    void delete(String username);
}
