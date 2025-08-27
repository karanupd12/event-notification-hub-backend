package com.ednh.websocket;

import com.ednh.entity.User;
import com.ednh.service.JwtService;
import com.ednh.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * WebSocket channel interceptor for JWT authentication
 * Validates JWT tokens for WebSocket connections
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");

            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);

                try {
                    String username = jwtService.extractUsername(token);
                    User user = (User) userService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(token, user)) {
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());
                        accessor.setUser(authentication);

                        log.info("WebSocket connection authenticated for user: {}", username);
                    }
                } catch (Exception e) {
                    log.error("WebSocket authentication failed: {}", e.getMessage());
                    return null; // Reject connection
                }
            } else {
                log.warn("WebSocket connection attempted without valid Authorization header");
                return null; // Reject connection
            }
        }

        return message;
    }
}
