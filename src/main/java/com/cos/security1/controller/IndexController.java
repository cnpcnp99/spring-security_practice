package com.cos.security1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {


    @GetMapping({"", "/"}) // localhost:8080/ or localhost:8080
    public String index() {
        return "index";
    }
}
