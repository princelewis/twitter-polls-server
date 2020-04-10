package com.twitter.polls.payload;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;

}
