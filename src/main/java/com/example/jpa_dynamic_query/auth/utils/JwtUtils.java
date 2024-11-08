package com.example.jpa_dynamic_query.auth.utils;

import com.example.jpa_dynamic_query.auth.detail.UserDetailsImpl;
import com.example.jpa_dynamic_query.entity.User;
import com.example.jpa_dynamic_query.enums.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import oracle.security.o3logon.a;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.example.jpa_dynamic_query.enums.TokenType.ACCESS_TOKEN;
import static com.example.jpa_dynamic_query.enums.TokenType.REFRESH_TOKEN;


@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwt.accessKey}")
    private String accessKey;

    @Value("${app.jwt.refreshKey}")
    private String refreshKey;

    @Value("${app.jwt.jwtExpirationMs}")
    private Long jwtExpirationMs;

    public String generateAccessToken(UserDetailsImpl user){
        Map<String, Object> claims = Map.of("Id", user.getId());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuer("jpa-dynamic-query")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key(ACCESS_TOKEN), SignatureAlgorithm.HS512)
                .compact();
    }
    public String generateRefreshToken( UserDetails user){
        Map<String, Object> claims= new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuer("jpa-dynamic-query")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs * 14))
                .signWith(key(REFRESH_TOKEN), SignatureAlgorithm.HS512)
                .compact();
    }
    public  <T> T extractClaims(String token, TokenType tokenType, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsResolver.apply(claims);
    }
    public Claims extractAllClaims(String token, TokenType tokenType){
        return Jwts.parserBuilder().setSigningKey(key(tokenType)).build().parseClaimsJws(token).getBody();
    }
    public String getUserNameFromJwtToken(String token, TokenType tokenType) {
        return Jwts.parserBuilder().setSigningKey(key(tokenType)).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    public Date extractExpiration(String token, TokenType tokenType){
        return extractClaims(token, tokenType, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token, TokenType tokenType){
        return extractExpiration(token, tokenType).before(new Date());
    }
    private Key key(TokenType tokenType) {
        byte[] keyByte;
        if (ACCESS_TOKEN.equals(tokenType)){
            keyByte = Decoders.BASE64.decode(accessKey);
        }else{
            keyByte = Decoders.BASE64.decode(refreshKey);
        }
        return Keys.hmacShaKeyFor(keyByte);
    }

    public boolean validateJwtToken(String authToken, TokenType tokenType, UserDetails userDetails){
        try {
            final String username = getUserNameFromJwtToken(authToken, tokenType);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(authToken, tokenType));
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


}
