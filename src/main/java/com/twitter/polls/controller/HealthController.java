package com.twitter.polls.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HealthController {


    @GetMapping("/health")
    public String health(){
        return "health check";
    }
}
