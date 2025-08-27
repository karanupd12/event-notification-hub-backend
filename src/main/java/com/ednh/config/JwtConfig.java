package com.ednh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties
 * Externalized configuration for security and flexibility
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String secret = "ednh-super-secret-key-change-in-production-minimum-256-bits";
    private long accessTokenExpiration = 900000; // 15 minutes
    private long refreshTokenExpiration = 604800000; // 7 days
    private String issuer = "event-notification-hub";
    private String audience = "ednh-users";
}
