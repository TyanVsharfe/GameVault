package com.gamevault.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.data_template.API_CLIENT;
import com.gamevault.data_template.GameInfo;
import com.gamevault.db.Game;
import com.gamevault.service.RequestService;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/games")
public class GameController {
    //private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping ("/{gameName}")
    public String list(Model model, @PathVariable("gameName") String gameName) throws JsonProcessingException, UnirestException {

        //ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //String json = restTemplate.getForObject(jsonResponse, String.class);

        //GameInfo gameInfo = objectMapper.readValue(jsonResponse.getBody(), GameInfo.class);
        //GameInfo gameInfo = new Gson().fromJson(jsonResponse.getBody().toString(), GameInfo.class);

        //model.addAttribute("gameName",gameInfo.getName());
        //model.addAttribute("gameBackgroundImage",gameInfo.getCover());
        //model.addAttribute("gameDescription",gameInfo.getSummary());
        //model.addAttribute("gameReleased",gameInfo.getReleaseDate());
        // model.addAttribute("gameMetacritic",gameInfo.getMetacritic());
        return "index";
    }

    @PostMapping()
    public Game chooseGame(String json) {
        return new Game();
    }
}
