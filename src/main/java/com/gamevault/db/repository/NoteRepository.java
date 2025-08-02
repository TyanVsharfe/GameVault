package com.gamevault.db.repository;

import com.gamevault.db.model.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {
    Iterable<Note> findAllByUserGame_IdAndUser_Id(Long gameId, UUID userId);
    Optional<Note> findByIdAndUser_Id(Long aLong, UUID userId);

    void deleteAllByUserGame_IdAndUser_Id(Long Id, UUID userId);
    void deleteByIdAndUser_Id(Long Id, UUID userId);
}
