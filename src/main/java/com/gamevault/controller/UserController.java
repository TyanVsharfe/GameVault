package com.gamevault.controller;

import com.gamevault.security.JwtUtil;
import com.gamevault.form.user.LoginRequest;
import com.gamevault.form.user.LogoutRequest;
import com.gamevault.form.user.RefreshRequest;
import com.gamevault.form.user.UserForm;
import com.gamevault.service.TokenBlacklistService;
import com.gamevault.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final UserService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public UserController(UserService userDetailsService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/registration")
    public String register(@RequestBody UserForm user) {
        return userDetailsService.addUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            if (request.username() == null || request.password() == null ||
                    request.username().trim().isEmpty() || request.password().trim().isEmpty()) {
                response.put("error", "Username or password is missing");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = request.remember_me() ? jwtUtil.generateRefreshToken(userDetails) : null;

            response.put("access_token", accessToken);
            if (refreshToken != null) {
                response.put("refresh_token", refreshToken);
            }

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (AuthenticationException e) {
            response.put("error", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        String token = request.token();
        if (token != null) {
            long remainingTime = jwtUtil.extractExpiration(token).getTime() - java.util.Date.from(Instant.now()).getTime();
            tokenBlacklistService.addToBlacklist(token, remainingTime);
        }

        String refreshToken = request.refresh_token();
        if (refreshToken != null) {
            long remainingTime = jwtUtil.extractExpiration(refreshToken).getTime() - java.util.Date.from(Instant.now()).getTime();
            tokenBlacklistService.addToBlacklist(refreshToken, remainingTime);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@Valid @RequestBody RefreshRequest request) {
        String refreshToken = request.refresh_token();

        if (tokenBlacklistService.isBlacklisted(request.refresh_token())) {
            return ResponseEntity.status(403).build();
        }

        if (jwtUtil.validateToken(refreshToken, userDetailsService.loadUserByUsername(jwtUtil.extractUsername(refreshToken)))) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtUtil.extractUsername(refreshToken));
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);

            Map<String, String> response = new HashMap<>();
            response.put("access_token", newAccessToken);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).build();
        }
    }
}
