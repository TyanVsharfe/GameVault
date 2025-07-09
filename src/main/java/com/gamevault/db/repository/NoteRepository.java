package com.gamevault.db.repository;

import com.gamevault.db.model.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {
    Iterable<Note> findAllByUserGame_Id(Long Id);
    void deleteAllByUserGame_Id(Long Id);
}
