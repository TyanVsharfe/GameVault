package com.gamevault.controller;

import com.gamevault.data_template.API_CLIENT;
import com.gamevault.service.RequestService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RESTController {

    @GetMapping("/game/{id}")
    public int getGame(@PathVariable("id") Long id) {
        return 0;
    }

    @GetMapping("/games")
    public int getGames() {
        return 0;
    }

    @PostMapping("/game")
    public int postGame() {
        return 0;
    }

    @DeleteMapping("/game/{id}")
    public int deleteGame(@PathVariable("id") Long id) {
        return 0;
    }

    @PutMapping("/game/{id}")
    public int putGame(@PathVariable("id") Long id) {
        return 0;
    }
}
