package com.gamevault.service;

import com.gamevault.db.model.User;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.form.UserForm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with username" +  username + " not found");
        }

        User exUser = user.get();

        List<GrantedAuthority> authorities = exUser.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                exUser.getUsername(),
                exUser.getPassword(),
                authorities);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public String addUser(UserForm user) {
        if (userRepository.findByUsername(user.username()).isPresent()) {
            return "Username already exists";
        }
        String bcryptPass = new BCryptPasswordEncoder().encode(user.password());
        User newUser = new User(user.username(), bcryptPass, List.of("ROLE_USER"));
        userRepository.save(newUser);
        return "User registered successfully";
    }
}
