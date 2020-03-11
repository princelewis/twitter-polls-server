package com.twitter.polls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

//This annotation contains @Configuration, @EnableAutoConfiguration,
//and @ComponentScan
@SpringBootApplication

//This tells springboot where to get Jsr310JpaConverters.class
//so it can be used to convert dates created on our entity classes
//to MySql datetime data type when persisting the entity on the database
@EntityScan(
        basePackageClasses = {
                Jsr310JpaConverters.class
        }
)
public class PollsApplication {

    //This method runs once, immediately after dependency injection occurs
    //The purpose is to set the default time of the application to UTC
    @PostConstruct
    void init() {

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(PollsApplication.class, args);
    }

}
