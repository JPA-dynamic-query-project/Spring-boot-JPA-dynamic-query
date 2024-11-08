package com.example.jpa_dynamic_query.service.Impl;

import com.example.jpa_dynamic_query.entity.Token;
import com.example.jpa_dynamic_query.exception.AppException;
import com.example.jpa_dynamic_query.repository.TokenRepository;
import com.example.jpa_dynamic_query.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    @Override
    public Token getTokenByUsername(String username) {
        return tokenRepository.findByUsername(username)
                .orElseThrow(()-> new AppException("Token not found"));
    }

    @Override
    public int save(Token token) {
        Optional<Token> tokenOptional = tokenRepository.findByUsername(token.getUsername());
        if (tokenOptional.isEmpty()){
            tokenRepository.save(token);
            return token.getId();
        }else {
            Token t = tokenOptional.get();
            t.setAccessToken(token.getAccessToken());
            t.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(t);
            return t.getId();
        }
    }

    @Override
    public void delete(String username) {
        Token token = getTokenByUsername(username);
        tokenRepository.delete(token);
    }
}
