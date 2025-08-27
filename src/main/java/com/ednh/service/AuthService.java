package com.ednh.service;

import com.ednh.dto.request.LoginRequest;
import com.ednh.dto.request.RefreshTokenRequest;
import com.ednh.dto.request.RegisterRequest;
import com.ednh.dto.response.AuthResponse;
import com.ednh.dto.response.UserResponse;
import com.ednh.entity.RefreshToken;
import com.ednh.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication service handling login, registration, and token refresh
 * Coordinates between user management, JWT, and refresh token services
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate unique constraints
        if (userService.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(User.Role.USER)
                .tenantId(request.getTenantId())
                .enabled(true)
                .build();

        user = userService.save(user);
        log.info("New user registered: {}", user.getUsername());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .user(UserResponse.fromUser(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = (User) userService.loadUserByUsername(request.getUsernameOrEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        log.info("User logged in: {}", user.getUsername());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .user(UserResponse.fromUser(user))
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();

        // Generate new access token
        String accessToken = jwtService.generateAccessToken(user);

        log.info("Token refreshed for user: {}", user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken()) // Keep the same refresh token
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .user(UserResponse.fromUser(user))
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            refreshTokenService.revokeByToken(refreshToken);
        }
    }
}
