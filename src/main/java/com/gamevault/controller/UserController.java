package com.gamevault.controller;

import com.gamevault.dto.input.UserForm;
import com.gamevault.dto.input.UserFormLogin;
import com.gamevault.service.email.EmailVerificationTokenService;
import com.gamevault.service.user.AuthService;
import com.gamevault.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final EmailVerificationTokenService mailEmailVerificationTokenService;

    public UserController(UserService userService, AuthService authService, EmailVerificationTokenService mailEmailVerificationTokenService) {
        this.userService = userService;
        this.authService = authService;
        this.mailEmailVerificationTokenService = mailEmailVerificationTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserFormLogin formLogin,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
        authService.login(formLogin, request, response);
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok(Map.of("message", "Logout successful."));
    }

    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserForm user) {
        userService.register(user);
        return ResponseEntity.ok(Map.of("message", "Register in process. Check your email."));
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        mailEmailVerificationTokenService.verify(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-session")
    @ResponseStatus(HttpStatus.OK)
    public void checkSession() {
    }
}
