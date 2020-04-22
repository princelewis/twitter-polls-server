package com.twitter.polls.controller;

import com.twitter.polls.model.Role;
import com.twitter.polls.model.RoleName;
import com.twitter.polls.model.User;
import com.twitter.polls.payload.ApiResponse;
import com.twitter.polls.payload.JwtAuthenticationResponse;
import com.twitter.polls.payload.LoginRequest;
import com.twitter.polls.payload.SignUpRequest;
import com.twitter.polls.repository.RoleRepository;
import com.twitter.polls.repository.UserRepository;
import com.twitter.polls.security.CustomUserDetailsService;
import com.twitter.polls.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest signUpRequest) throws Exception {
        Boolean emailExists = userRepository.existsByEmail(signUpRequest.getEmail());
        Boolean usernameExists = userRepository.existsByUsername(signUpRequest.getUsername());
        if(emailExists){
            ApiResponse apiResponse = new ApiResponse(false,"Email is already in use" );
            return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
        } else if(usernameExists){
            ApiResponse apiResponse = new ApiResponse(false,"Username is already in use" );
            return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
        } else{
            Role role =  roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RoleNotFoundException("ROLE_USER not found"));

            String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());

            User user = new User(signUpRequest.getName(),
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    hashedPassword
            );
//            Set<Role> userRole = new HashSet<Role>(){{add(role);}};
            user.setRole(Collections.singleton(role));

            userRepository.save(user);
            ApiResponse apiResponse = new ApiResponse(true,"User successfully registered");

            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse(jwt);

        return new ResponseEntity<>(jwtAuthenticationResponse,HttpStatus.OK);
    }

}
