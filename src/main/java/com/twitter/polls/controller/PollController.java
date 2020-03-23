package com.twitter.polls.controller;

import com.twitter.polls.dto.LoginRequest;
import com.twitter.polls.model.User;
import com.twitter.polls.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/polls")
public class PollController {

    private UserRepository userRepository;
    @Autowired
    public PollController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello World!";
    }

    @PostMapping("/table")
    public String postTable(@RequestBody User user){
        userRepository.save(user);
        return "Done";

    }

    @PutMapping("/update")
    public String updateTable(){
        User user = userRepository.findById(1l).orElseThrow(() -> new Error("Not available"));
        user.setName("nkechi");
        userRepository.save(user);
        return "updated";
    }
}
