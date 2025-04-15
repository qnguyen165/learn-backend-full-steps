package com.qnguyendev.backendservice.service;

import com.qnguyendev.backendservice.controller.SignInRequest;
import com.qnguyendev.backendservice.dto.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse getAccessToken(SignInRequest signInRequest);
    TokenResponse getRefreshToken(String signInRequest);
}
