package com.gamevault.controller;

import com.gamevault.db.model.User;
import com.gamevault.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public String registerUser(@RequestBody User user) {
        return user.toString();
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        return user.toString();
    }
}
