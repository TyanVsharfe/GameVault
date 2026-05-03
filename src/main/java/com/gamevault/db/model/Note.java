package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gamevault.dto.input.NoteForm;
import com.gamevault.dto.input.update.NoteUpdateForm;
import com.gamevault.enums.Enums;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "notes")
public class Note extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String content;
    private Enums.NoteType type = Enums.NoteType.GENERAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_id", referencedColumnName = "id")
    @JsonBackReference
    private UserGame userGame;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Note(NoteForm noteForm, UserGame userGame, User user) {
        this.title = noteForm.title();
        this.content = noteForm.content();
        this.type = noteForm.type();
        this.userGame = userGame;
        this.user = user;
    }

    public void updateDto(NoteUpdateForm updateForm) {
        if (updateForm.title() != null) {
            this.title = updateForm.title();
        }
        if (updateForm.content() != null) {
            this.content = updateForm.content();
        }
        if (updateForm.type() != null) {
            this.type = updateForm.type();
        }
    }
}
