package com.gamevault.dto.input;

import com.gamevault.enums.Enums;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UserGamesFilterParams {
    private Enums.Status status;
    private Integer minRating;
    private Integer maxRating;
    private Boolean hasReview;
    private Boolean isFullyCompleted;
    private Enums.CategoryIGDB gameType;
    private String title;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAfter;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate  createdBefore;
}
