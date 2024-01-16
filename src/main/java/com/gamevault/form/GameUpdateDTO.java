package com.gamevault.form;

import com.gamevault.data_template.Enums;
import java.util.Optional;

public record GameUpdateDTO(Long id, Optional<Enums.status> status, Optional<Double> userRating) {
}
