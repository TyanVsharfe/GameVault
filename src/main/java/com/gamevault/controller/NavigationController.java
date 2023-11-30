package com.gamevault.controller;

import com.gamevault.data_template.GameInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.jws.WebParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

        //String jsonString =
        //Type listType = new TypeToken<ArrayList<GameInfo>>(){}.getType();

        //ArrayList<GameInfo> gameInfoArrayList = new Gson().fromJson(jsonString, listType);
        return "search";
    }
}
