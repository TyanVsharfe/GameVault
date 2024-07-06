package com.gamevault.controller.rest;

import com.gamevault.db.model.Note;
import com.gamevault.form.GameForm;
import com.gamevault.form.NoteForm;
import com.gamevault.service.NoteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NoteRestController {
    private final NoteService noteService;

    public NoteRestController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/game/note/{game_id}")
    public Iterable<Note> getAllByIgdbId(@PathVariable("game_id") Long igdbId) {
        return noteService.getAllNotesByIgdbId(igdbId);
    }

    @PostMapping("/game/note")
    public Note add(@RequestBody NoteForm noteForm) {
        return noteService.addNote(noteForm);
    }

    @DeleteMapping("/game/note/{id}")
    public void delete(@PathVariable("id") Long id) {
        noteService.deleteNote(id);
    }

    @DeleteMapping("/game/notes/{igdbId}")
    public void deleteAllByIgdbId(@PathVariable("igdbId") Long igdbId) {
        noteService.deleteAllGameNotes(igdbId);
    }
}
