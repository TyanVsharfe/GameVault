package com.gamevault.form;

import java.util.Optional;

public record NoteUpdateDTO(Optional<String> title, Optional<String> content) {
}
