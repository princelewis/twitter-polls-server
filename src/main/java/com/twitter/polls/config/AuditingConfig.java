package com.twitter.polls.config;

import com.twitter.polls.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<Long> auditorProvider(){
        return new SpringSecurityAuditAwareImpl();
    }


    //Here we are configuring the AuditorAware interface to
    //get the details of the authenticated user
    //The main purpose of this is to populate the polls table with the ID of the user
    //who created and or updated a poll.
class SpringSecurityAuditAwareImpl implements AuditorAware<Long>{
    @Override
    public Optional<Long> getCurrentAuditor() {

        //From the SecurityContext get the details of an already authenticated user.
        //From the details of an authenticated user, you can get their ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken){
            return Optional.empty();
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Optional.ofNullable(userPrincipal.getId());

    }
}
}


