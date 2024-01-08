package com.gamevault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping( "/")
public class NavigationController {
    @GetMapping("main")
    public String menu() {
        return "index";
    }

    @GetMapping("/account")
    public String account() {
        return "account";
    }

    @GetMapping("/search")
    public String searchGet() {
        return "search";
    }

    @PostMapping("/search")
    public String searchPost(Model model) {
        return "search";
    }

    @GetMapping("/game/{id}")
    public String gamePage(@PathVariable String id) {
        return "game-page";
    }
}
