package com.ednh.controller;

import com.ednh.dto.request.LoginRequest;
import com.ednh.dto.request.RefreshTokenRequest;
import com.ednh.dto.request.RegisterRequest;
import com.ednh.dto.response.ApiResponse;
import com.ednh.dto.response.AuthResponse;
import com.ednh.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints
 * Handles user registration, login, token refresh, and logout
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Registration attempt for username: {}", request.getUsername());

        AuthResponse authResponse = authService.register(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "User registered successfully",
                authResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Login attempt for: {}", request.getUsernameOrEmail());

        AuthResponse authResponse = authService.login(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Login successful",
                authResponse
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        log.info("Token refresh attempt");

        AuthResponse authResponse = authService.refreshToken(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Token refreshed successfully",
                authResponse
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestBody(required = false) RefreshTokenRequest request) {

        String refreshToken = request != null ? request.getRefreshToken() : null;
        authService.logout(refreshToken);

        ApiResponse<String> response = ApiResponse.success(
                "Logout successful",
                null
        );

        return ResponseEntity.ok(response);
    }
}
