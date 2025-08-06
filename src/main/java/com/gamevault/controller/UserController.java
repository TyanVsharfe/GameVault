package com.gamevault.controller;

import com.gamevault.form.UserForm;
import com.gamevault.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenBasedRememberMeServices rememberMeServices;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public UserController(UserService userService, AuthenticationManager authenticationManager, TokenBasedRememberMeServices rememberMeServices) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.rememberMeServices = rememberMeServices;
    }

    @PostMapping("/registration")
    public String register(@RequestBody UserForm user) {
        return userService.addUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username,
                                                     @RequestParam String password,
                                                     @RequestParam(value = "remember-me", required = false) String rememberMe,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
        Map<String, String> responseBody = new HashMap<>();

        try {
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                responseBody.put("error", "Username or password is missing");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
            }

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);

            Authentication authResult = authenticationManager.authenticate(authRequest);

            SecurityContextHolder.getContext().setAuthentication(authResult);

            request.getSession(true);

            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

            if ("true".equals(rememberMe) || "on".equals(rememberMe)) {
                rememberMeServices.loginSuccess(request, response, authResult);
            }

            responseBody.put("message", "Login successful");
            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException e) {
            responseBody.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        } catch (AuthenticationException e) {
            responseBody.put("error", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        rememberMeServices.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/check-session")
    public ResponseEntity<Void> checkSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
