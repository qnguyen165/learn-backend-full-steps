package com.qnguyendev.backendservice.service.impl;

import com.qnguyendev.backendservice.common.TokenType;
import com.qnguyendev.backendservice.exeception.InvalidDataException;
import com.qnguyendev.backendservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiry-minutes}")
    private long expiryMinutes;

    @Value("${jwt.expiry-days}")
    private long expiryDays;

    @Value("${jwt.access-key}")
    private String accessKey;

    @Value("${jwt.refresh-key}")
    private String refreshKey;

    private static final long ONE_MINUTE = 60 * 1000;
    private static final long ONE_DAY = ONE_MINUTE * 60 * 24;

    @Override
    public String generateAccessToken(String username, List<String> authorities) {
        log.info("Generate access token for user {} with authorities {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", authorities);

        return generateAccessToken(claims, username);
    }

    @Override
    public String generateRefreshToken(String username, List<String> authorities) {
        log.info("Generate refresh token for user {} with authorities {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", authorities);

        return generateRefreshToken(claims, username);
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        log.info("Extract username from token {} with type {}", token, type);

        return extractClaims(type, token, Claims::getSubject);
    }

    private <T> T extractClaims(TokenType type, String token, Function<Claims, T> claimsExtractor) {
        final Claims claims = extractAllClaims(token, type);
        return claimsExtractor.apply(claims);
    }

    private Claims extractAllClaims(String token, TokenType type) {
        log.info("Extract all claims from token {} with type {}", token, type);

        try {
            return Jwts.parserBuilder()
                       .setSigningKey(getKey(type))
                       .build()
                       .parseClaimsJws(token)
                       .getBody();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access token is expired or invalid");
        }

    }

    private String generateAccessToken(Map<String, Object> claims, String username) {
        log.info("Generate token with claims {} and name {}", claims, username);
        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(username)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + ONE_MINUTE * expiryMinutes))
                   .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)// 1 hour
                   .compact();
    }

    private String generateRefreshToken(Map<String, Object> claims, String username) {
        log.info("Generate token with claims {} and name {}", claims, username);
        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(username)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + ONE_DAY * expiryDays))
                   .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)// 1 hour
                   .compact();
    }

    private Key getKey(TokenType type) {
        switch (type) {
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default -> throw new InvalidDataException("Invalid token type");
        }
    }
}
