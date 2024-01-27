package com.gamevault.controller.rest;

import com.gamevault.db.model.Note;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class NoteRestController {
    // TODO Сделать реализацию
    @GetMapping("/game/note/{game_id}")
    public Optional<Note> get(@PathVariable("game_id") Long id) {
        Optional<Note> note = Optional.empty();
        return note;
    }
}
