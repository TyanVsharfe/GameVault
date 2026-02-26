package com.gamevault.controller;

import com.gamevault.dto.input.UserForm;
import com.gamevault.dto.input.UserFormLogin;
import com.gamevault.service.email.EmailVerificationTokenService;
import com.gamevault.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final EmailVerificationTokenService mailEmailVerificationTokenService;
    private final AuthenticationManager authenticationManager;
    private final TokenBasedRememberMeServices rememberMeServices;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public UserController(UserService userService, EmailVerificationTokenService mailEmailVerificationTokenService, AuthenticationManager authenticationManager, TokenBasedRememberMeServices rememberMeServices) {
        this.userService = userService;
        this.mailEmailVerificationTokenService = mailEmailVerificationTokenService;
        this.authenticationManager = authenticationManager;
        this.rememberMeServices = rememberMeServices;
    }

    @PostMapping("/registration")
    public void register(@RequestBody UserForm user) {
        userService.register(user);
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        mailEmailVerificationTokenService.verify(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserFormLogin formLogin,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
        Map<String, String> responseBody = new HashMap<>();
        String username = formLogin.username();
        String password = formLogin.password();
        String rememberMe = formLogin.rememberMe();

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
    @ResponseStatus(HttpStatus.OK)
    public void checkSession() {
    }
}
