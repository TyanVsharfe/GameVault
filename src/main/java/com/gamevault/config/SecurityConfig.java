package com.gamevault.config;

import com.gamevault.service.UserService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userDetailsService;
    private final HandlerMappingIntrospector handlerMappingIntrospector;

    public SecurityConfig(UserService userDetailsService, HandlerMappingIntrospector handlerMappingIntrospector) {
        this.userDetailsService = userDetailsService;
        this.handlerMappingIntrospector = handlerMappingIntrospector;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> {
//                    auth
//                            .requestMatchers(new MvcRequestMatcher(introspector, "/register"),
//                                    new MvcRequestMatcher(introspector, "/login"))
//                            .permitAll()
//
//                            .anyRequest().authenticated();
//                })
//                .formLogin(form -> {
//                    form
//                            .loginPage("/login")
//                            .permitAll();
//                })
//                .logout(LogoutConfigurer::permitAll);
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers(new MvcRequestMatcher(handlerMappingIntrospector, "/registration"), new MvcRequestMatcher(handlerMappingIntrospector, "/login"))
                        .permitAll() // Разрешаем доступ к этим эндпоинтам без авторизации
                .anyRequest().authenticated(); // Все остальные запросы требуют авторизации

        // Если вы хотите отключить CSRF, раскомментируйте следующую строку
        // http.csrf().disable();

        return http.build();
    }


    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
