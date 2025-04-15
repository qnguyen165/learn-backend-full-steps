package com.qnguyendev.backendservice.service;

import com.qnguyendev.backendservice.dto.request.UserCreationRequest;
import com.qnguyendev.backendservice.dto.request.UserPasswordRequest;
import com.qnguyendev.backendservice.dto.request.UserUpdateRequest;
import com.qnguyendev.backendservice.dto.response.UserPageResponse;
import com.qnguyendev.backendservice.dto.response.UserResponse;

public interface UserService {

    UserPageResponse findAll(String keyword, String sort, int page, int size);

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    UserResponse findByEmail(String email);

    long save(UserCreationRequest req);

    void update(UserUpdateRequest req);

    void changePassword(UserPasswordRequest req);

    void delete(Long id);
}
