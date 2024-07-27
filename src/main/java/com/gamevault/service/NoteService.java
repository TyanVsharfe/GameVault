package com.gamevault.service;

import com.gamevault.db.model.Game;
import com.gamevault.db.model.Note;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.db.repository.NoteRepository;
import com.gamevault.form.NoteForm;
import com.gamevault.form.NoteUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final GameRepository gameRepository;

    public NoteService(NoteRepository noteRepository, GameRepository gameRepository) {
        this.noteRepository = noteRepository;
        this.gameRepository = gameRepository;
    }

    public Iterable<Note> getAllNotesByIgdbId(Long igdbId) {
        return noteRepository.findAllByGame_IgdbId(igdbId);
    }

    @Transactional
    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllGameNotes(Long igdbId) {
        noteRepository.deleteAllByGame_IgdbId(igdbId);
    }

    public Note addNote(NoteForm noteForm) {
        Game game = gameRepository.findGameByIgdbId(noteForm.igdbId()).orElseThrow(() ->
                new EntityNotFoundException("Game with igdbId " + noteForm.igdbId() + " not found"));
        return noteRepository.save(new Note(noteForm, game));
    }

    public void updateNote(Long id, NoteUpdateDTO noteUpdateDTO) {
        Note note = noteRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Note with id " + id + " not found"));
        note.setContent(noteUpdateDTO.content().orElse(note.getContent()));

        System.out.println("Заметка изменена");
        System.out.println("Id " + note.getId() + " Content " + note.getContent());
        noteRepository.save(note);
    }
}
