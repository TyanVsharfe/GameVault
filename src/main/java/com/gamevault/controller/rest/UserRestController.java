package com.gamevault.controller.rest;

import com.gamevault.form.UserForm;
import com.gamevault.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public String registerUser(@RequestBody UserForm user) {
        return userService.addUser(user);
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody UserForm user) {
        return userService.getUser(user);
    }
}
