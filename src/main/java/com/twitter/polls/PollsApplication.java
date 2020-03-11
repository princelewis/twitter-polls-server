package com.twitter.polls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


//This houses @EnableAutoConfiguration
//@Configuration
//@ComponentScan
@SpringBootApplication

//The main purpose of this is to access the class Jsr310JpaConverter
//to convert the date classes we have on our entity to MySql
//date-type when during persistence
@EntityScan(basePackageClasses = Jsr310JpaConverters.class)
public class PollsApplication {

    @PostConstruct
    void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(PollsApplication.class, args);
    }

}
