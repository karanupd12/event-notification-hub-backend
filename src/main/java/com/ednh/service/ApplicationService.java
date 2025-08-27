package com.ednh.service;

import com.ednh.entity.Application;
import com.ednh.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing webhook applications
 * Handles application validation and JWT verification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JwtService jwtService;

    public Optional<Application> findByAppId(String appId) {
        return applicationRepository.findByAppIdAndEnabled(appId, true);
    }

    public boolean validateApplicationToken(String appId, String token) {
        Optional<Application> appOpt = findByAppId(appId);

        if (appOpt.isEmpty()) {
            log.warn("Application not found or disabled: {}", appId);
            return false;
        }

        Application app = appOpt.get();

        try {
            // Validate JWT token using application's secret
            String username = jwtService.extractUsername(token);

            // For application tokens, username should match appId
            if (!appId.equals(username)) {
                log.warn("Token username mismatch for app: {}", appId);
                return false;
            }

            // Update last used timestamp
            app.setLastUsedAt(LocalDateTime.now());
            applicationRepository.save(app);

            return true;
        } catch (Exception e) {
            log.error("Token validation failed for app {}: {}", appId, e.getMessage());
            return false;
        }
    }

    public void incrementNotificationCount(String appId) {
        Optional<Application> appOpt = findByAppId(appId);
        if (appOpt.isPresent()) {
            Application app = appOpt.get();
            app.setTotalNotificationsSent(app.getTotalNotificationsSent() + 1);
            applicationRepository.save(app);
        }
    }
}
