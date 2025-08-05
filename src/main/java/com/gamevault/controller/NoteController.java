package com.gamevault.controller;

import com.gamevault.db.model.Note;
import com.gamevault.db.model.User;
import com.gamevault.form.NoteForm;
import com.gamevault.form.update.NoteUpdateForm;
import com.gamevault.service.NoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{igdbId}")
    public Iterable<Note> getAllByIgdbId(@PathVariable("igdbId") Long igdbId,
                                         @AuthenticationPrincipal User user) {
        return noteService.getAllNotesByIgdbId(igdbId, user);
    }

    @PostMapping("/{igdbId}")
    public Note add(@RequestBody NoteForm noteForm, @PathVariable("igdbId") Long igdbId,
                    @AuthenticationPrincipal User user) {
        return noteService.addNote(noteForm, igdbId, user);
    }

    @PutMapping("/{note_id}")
    public void put(@PathVariable("note_id") Long id, @RequestBody NoteUpdateForm noteUpdateForm,
                    @AuthenticationPrincipal User user) {
        noteService.updateNote(id, noteUpdateForm, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id,
                       @AuthenticationPrincipal User user) {
        noteService.deleteNote(id, user);
    }

    @DeleteMapping("/{igdbId}/all")
    public void deleteAllByIgdbId(@PathVariable("igdbId") Long igdbId,
                                  @AuthenticationPrincipal User user) {
        noteService.deleteAllGameNotes(igdbId, user);
    }
}
