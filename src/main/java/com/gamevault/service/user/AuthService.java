package com.gamevault.service.user;

import com.gamevault.dto.input.UserFormLogin;
import com.gamevault.metrics.CustomMetrics;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final TokenBasedRememberMeServices rememberMeServices;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final CustomMetrics customMetrics;

    public AuthService(TokenBasedRememberMeServices rememberMeServices, AuthenticationManager authenticationManager, CustomMetrics customMetrics) {
        this.rememberMeServices = rememberMeServices;
        this.authenticationManager = authenticationManager;
        this.customMetrics = customMetrics;
    }

    public void login(UserFormLogin formLogin, HttpServletRequest request, HttpServletResponse response) {
        String username = formLogin.username();
        String password = formLogin.password();
        String rememberMe = formLogin.rememberMe();

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username or password is missing");
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

        customMetrics.incrementUserAuth("login");
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        rememberMeServices.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }
}
