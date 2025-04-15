package com.qnguyendev.backendservice.service;

import com.qnguyendev.backendservice.common.TokenType;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface JwtService {

    String generateAccessToken(String username, List<String> authorities);
    String generateRefreshToken(String username, List<String> authorities);
    String extractUsername(String token, TokenType type);
}
