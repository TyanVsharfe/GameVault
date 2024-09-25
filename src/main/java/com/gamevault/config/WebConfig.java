package com.gamevault.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // разрешаем CORS для всех эндпоинтов, начинающихся с /api/
                .allowedOrigins("http://localhost:3000") // разрешаем запросы только с этого домена
                .allowedMethods("GET", "POST", "PUT", "DELETE") // разрешаем указанные HTTP методы
                .allowCredentials(true); // разрешаем передавать куки и заголовки авторизации
    }
}
