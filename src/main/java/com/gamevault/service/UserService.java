package com.gamevault.service;

import com.gamevault.db.model.User;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.form.UserForm;
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
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
