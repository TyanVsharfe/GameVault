package com.gamevault.service;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGame;
import com.gamevault.db.model.Note;
import com.gamevault.db.repository.UserGameRepository;
import com.gamevault.db.repository.NoteRepository;
import com.gamevault.form.NoteForm;
import com.gamevault.form.update.NoteUpdateForm;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserGameRepository userGameRepository;

    public NoteService(NoteRepository noteRepository, UserGameRepository userGameRepository) {
        this.noteRepository = noteRepository;
        this.userGameRepository = userGameRepository;
    }

    public Iterable<Note> getAllNotesByIgdbId(Long igdbId, User user) {
        return noteRepository.findAllByUserGame_IdAndUser_Id(igdbId, user.getId());
    }

    @Transactional
    public void deleteNote(Long id, User user) {
        noteRepository.deleteByIdAndUser_Id(id, user.getId());
    }

    @Transactional
    public void deleteAllGameNotes(Long igdbId, User user) {
        noteRepository.deleteAllByUserGame_IdAndUser_Id(igdbId, user.getId());
    }

    public Note addNote(NoteForm noteForm, Long igdbId, User user) {
        UserGame userGame = userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, user.getUsername()).orElseThrow(() ->
                new EntityNotFoundException("Game with igdbId " + igdbId + " not found"));
        return noteRepository.save(new Note(noteForm, userGame, user));
    }

    public void updateNote(Long id, NoteUpdateForm noteUpdateForm, User user) {
        Note note = noteRepository.findByIdAndUser_Id(id, user.getId()).orElseThrow(
                () -> new EntityNotFoundException("Note with id " + id + " not found"));

        if (noteUpdateForm.title() != null) note.setTitle(noteUpdateForm.title());
        if (noteUpdateForm.content() != null) note.setContent(noteUpdateForm.content());

        noteRepository.save(note);
        log.info("Updated note with id={} for UserGame id={}:", id, note.getUserGame().getGame().getIgdbId());
    }
}
