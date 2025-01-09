package com.gamevault.service;

import com.gamevault.db.model.User;
import com.gamevault.db.repository.UserRepository;
import com.gamevault.form.UserForm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return (UserDetails) user.get();
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public String getUser(UserForm userForm) {

        Optional<User> user = userRepository.findByUsername(userForm.username());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with username" +  userForm.username() + " not found");
        }
        boolean isMatch = new BCryptPasswordEncoder().matches(userForm.password(), user.get().getPassword());
        if (!isMatch) {
            throw new UsernameNotFoundException("Wrong password");
        }

        return "Login successfully";
    }

    public String addUser(UserForm user) {
        if (userRepository.findByUsername(user.username()).isPresent()) {
            return "Username already exists";
        }
        String bcryptPass = new BCryptPasswordEncoder().encode(user.password());
        User newUser = new User(user.username(), bcryptPass);
        userRepository.save(newUser);
        return "User registered successfully";
    }
}
