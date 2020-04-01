package com.twitter.polls.security;

import com.twitter.polls.model.User;
import com.twitter.polls.repository.UserRepository;
import jdk.internal.org.jline.reader.UserInterruptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;


//This class implements the UserDetailsService interface which is
//the interface AuthenticationManger needs to access the database to
//authenticate a user. Look at it like a service class for retrieving data
//from the database
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        //Let people login with either username or email
        User user = userRepository.findByUsername(usernameOrEmail).
                orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username " + usernameOrEmail));
        return UserPrincipal.create(user);
    }

    //This method is used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserInterruptException("user not found with id :" + id));
        return UserPrincipal.create(user);
    }
}
