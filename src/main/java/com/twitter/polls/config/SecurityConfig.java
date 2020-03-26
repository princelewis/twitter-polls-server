package com.twitter.polls.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//Here is where you configure your spring boot security to use JWT for authentication
//Here we configure the basic authentication that the WebSecurityConfigureAdapter provides to:
//One, override the AuthenticationMangerBean method in other to push the AuthenticationManager to the Bean Factory in
//the IOC container. This authenticationManger will also be built here using AuthenticationMangerBuilder present in the
//configure method provided by the WebSecurityConfigurerAdapter
//Two, configure some API endpoints you might want to give total permission to for every user
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }

    //Here you build the authenticationManger by setting the userDetailService it needs to the customised one you
    //created - in this case, customerUserDetailsService - and the passwordEncoder it needs - in this case, the passwordEncoder
    //you pushed to the Bean Factory
    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customerUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
         return super.authenticationManagerBean();
    }

    //Set the kind of passwordEncoder that you wish to use for authentication to the Bean Factory
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http)throws Exception{
        http.cors().and().csrf().disable();
        http.
                exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatcher("/", "/favico.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.css",
                        "/**/*.html",
                        "/**/*.js")
                .permitAll()
                .antMatcher("/api/auth/**")
                .permitAll()
                .antMatcher("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
                .permitAll()
                .antMatcher(HttpMethod.GET,"/api/polls/**", "/api/users/**")
                .permitAll()
                .anyRequest()
                .authenticated();

        //Add our customer JWT security filter
        //Here we add JwtAuthenticationFilter before we call the authenticated method.
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
    }
}
