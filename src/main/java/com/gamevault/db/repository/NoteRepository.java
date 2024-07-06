package com.gamevault.db.repository;

import com.gamevault.db.model.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {
    Iterable<Note> findAllByGame_IgdbId(Long igdbId);
    void deleteAllByGame_IgdbId(Long igdbId);
}
