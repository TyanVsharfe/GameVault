package com.gamevault.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import com.gamevault.enums.Enums;

@Configuration
public class EnumConvertersConfig {

    @Bean
    public Converter<String, Enums.Status> stringToStatusConverter() {
        return new Converter<>() {
            @Override
            public Enums.Status convert(@NotNull String source) {
                return Enums.Status.fromJson(source);
            }
        };
    }

    @Bean
    public Converter<String, Enums.CategoryIGDB> stringToCategoryIgdbConverter() {
        return new Converter<>() {
            @Override
            public Enums.CategoryIGDB convert(@NotNull String source) {
                return Enums.CategoryIGDB.fromNumber(Integer.parseInt(source));
            }
        };
    }
}


