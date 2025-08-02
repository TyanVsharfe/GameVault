package com.gamevault.form;

import com.gamevault.data_template.Enums;
import com.gamevault.db.model.Note;

import java.util.List;
import java.util.Optional;

public record UserGameUpdateDTO(Optional<Enums.status> status, Optional<Boolean> fullyCompleted, Optional<Double> userRating, Optional<String> review, Optional<List<Note>> notes) {
}
