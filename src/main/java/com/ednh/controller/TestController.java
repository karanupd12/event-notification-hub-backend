package com.ednh.controller;

import com.ednh.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/public")
    public String publicEndpoint() {
        return "✅ Public endpoint working - no authentication required";
    }

    @GetMapping("/protected")
    public String protectedEndpoint(Principal principal) {
        return "✅ Protected endpoint working! Authenticated user: " + principal.getName();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint(Principal principal) {
        return "✅ Admin endpoint working! Admin user: " + principal.getName();
    }

    // Generate application token for webhook testing
    @GetMapping("/generate-app-token/{appId}")
    public Map<String, String> generateAppToken(@PathVariable String appId) {
        // Create a simple UserDetails-like object for the app
        org.springframework.security.core.userdetails.User appUser =
                new org.springframework.security.core.userdetails.User(
                        appId,
                        "",
                        java.util.Collections.emptyList()
                );

        String token = jwtService.generateAccessToken(appUser);

        Map<String, String> response = new HashMap<>();
        response.put("appId", appId);
        response.put("token", token);
        response.put("message", "Use this token in Authorization header for webhook calls");

        return response;
    }
}
