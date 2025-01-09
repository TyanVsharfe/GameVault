package com.gamevault.config;

import com.gamevault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import static com.gamevault.domain.ControllerNames.LOGIN_URL;
import static com.gamevault.domain.ControllerNames.REGISTRATION_URL;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationEntryPoint entryPoint;
    private final UserService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(AuthenticationEntryPoint entryPoint, UserService userDetailsService, PasswordEncoder passwordEncoder) {
        this.entryPoint = entryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry -> registry
                .requestMatchers(REGISTRATION_URL, LOGIN_URL).permitAll()
                .anyRequest().authenticated());
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);
        http.httpBasic(configure -> configure.authenticationEntryPoint(entryPoint));
        return http.build();
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authBuilder) throws Exception {
        authBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
