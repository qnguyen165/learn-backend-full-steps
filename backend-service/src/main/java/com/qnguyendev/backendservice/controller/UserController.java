package com.qnguyendev.backendservice.controller;

import com.qnguyendev.backendservice.dto.request.UserCreationRequest;
import com.qnguyendev.backendservice.dto.request.UserPasswordRequest;
import com.qnguyendev.backendservice.dto.request.UserUpdateRequest;
import com.qnguyendev.backendservice.dto.response.UserResponse;
import com.qnguyendev.backendservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@Slf4j(topic = "USER-CONTROLLER")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user list", description = "API retrieve user from database")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('manager')")
    public Map<String, Object> getList(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String sort,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        log.info("Get user list");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userService.findAll(keyword, sort, page, size));

        return result;
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by ID from database")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user')")
    public Map<String, Object> getUserDetail(@PathVariable @Min(1) Long userId) {
        log.info("Get user detail by ID: {}", userId);

        UserResponse userDetail = userService.findById(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user");
        result.put("data", userDetail);

        return result;
    }

    @Operation(summary = "Create User", description = "API add new user to database")
    @PostMapping("/add")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create User: {}", request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User created successfully");
        result.put("data", userService.save(request));

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Confirm Email", description = "Confirm email for account")
    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) throws IOException {
        log.info("Confirm email for account with secretCode: {}", secretCode);

        try {
            // TODO check or compare secret code from db
        } catch (Exception e) {
            log.error("Verification fail", e.getMessage(), e);
        } finally {
            response.sendRedirect("https://tayjava.vn/wp-admin/");
        }
    }

    @Operation(summary = "Update User", description = "API update user to database")
    @PutMapping("/upd")
    public Map<String, Object> updateUser(@RequestBody UserUpdateRequest request) {
        log.info("Updating user: {}", request);

        userService.update(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User updated successfully");
        result.put("data", "");

        return result;
    }

    @Operation(summary = "Change Password", description = "API change password for user to database")
    @PatchMapping("/change-pwd")
    public Map<String, Object> changePassword(@RequestBody UserPasswordRequest request) {
        log.info("Changing password for user: {}", request);

        userService.changePassword(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "Password updated successfully");
        result.put("data", "");

        return result;
    }

    @Operation(summary = "Delete user", description = "API activate user from database")
    @DeleteMapping("/del/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        log.info("Deleting user: {}", userId);

        userService.delete(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "User deleted successfully");
        result.put("data", "");

        return result;
    }
}
