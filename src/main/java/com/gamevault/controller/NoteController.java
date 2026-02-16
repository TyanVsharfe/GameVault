package com.gamevault.controller;

import com.gamevault.db.model.Note;
import com.gamevault.db.model.User;
import com.gamevault.dto.input.NoteForm;
import com.gamevault.dto.input.update.NoteUpdateForm;
import com.gamevault.service.NoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/games")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{igdb-id}/notes")
    public Iterable<Note> getAllByIgdbId(@PathVariable("igdb-id") Long igdbId,
                                         @AuthenticationPrincipal User user) {
        return noteService.getAllNotesByIgdbId(igdbId, user);
    }

    @PostMapping("/{igdb-id}/notes")
    public Note add(@RequestBody NoteForm noteForm, @PathVariable("igdb-id") Long igdbId,
                    @AuthenticationPrincipal User user) {
        return noteService.addNote(noteForm, igdbId, user);
    }

    @PutMapping("/notes/{note-id}")
    public void put(@PathVariable("note-id") Long id, @RequestBody NoteUpdateForm noteUpdateForm,
                    @AuthenticationPrincipal User user) {
        noteService.updateNote(id, noteUpdateForm, user);
    }

    @DeleteMapping("/notes/{note-id}")
    public void delete(@PathVariable("note-id") Long id,
                       @AuthenticationPrincipal User user) {
        noteService.deleteNote(id, user);
    }

    @DeleteMapping("/{igdb-id}/all")
    public void deleteAllByIgdbId(@PathVariable("igdb-id") Long igdbId,
                                  @AuthenticationPrincipal User user) {
        noteService.deleteAllGameNotes(igdbId, user);
    }
}
