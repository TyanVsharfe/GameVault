package com.gamevault.db.model;

import com.gamevault.enums.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "email_tokens")
public class EmailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.TokenType tokenType;

    @Column(nullable = false)
    private Instant expiresAt;

    @Setter
    @Column(nullable = false)
    private boolean used = false;

    public EmailToken(String token, User user,  Enums.TokenType tokenType, Instant expiresAt) {
        this.token = token;
        this.user = user;
        this.tokenType = tokenType;
        this.expiresAt = expiresAt;
        this.used = false;
    }
}
