package com.gamevault.db.model;

import com.gamevault.enums.Enums;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Setter
    @Column(unique = true, nullable = false)
    private String username;
    @Setter
    @Column(nullable = false)
    private String password;
    @Setter
    @Column(unique = true, nullable = false)
    private String email;
    @Setter
    private Enums.Subscription subscription;
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;
    @Setter
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean enabled = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    public User(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.subscription = Enums.Subscription.FREE;
    }

    public void createProfile() {
        if (this.profile == null) {
            this.profile = new UserProfile(this);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
