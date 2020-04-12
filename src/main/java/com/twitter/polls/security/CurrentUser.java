package com.twitter.polls.security;


//We are creating an annotation called CurrentUser. This annotation
//will be used to inject of know the current authenticated user
//in the controller

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

//This annotation is used to specify the nature of the target
//this annotation being created will operate on EX. this annotation
//will be used in the controller and inside the API handler
//parentheses to operate on the parameters of the handler
@Target({ElementType.PARAMETER, ElementType.TYPE})

//This annotation indicates how long or where the annotation in creation will
//live
@Retention(RetentionPolicy.RUNTIME)

@Documented

//This is the main annotation that injects user details in the
//controller
@AuthenticationPrincipal
public @interface CurrentUser {
}


//This reduces the dependency on Spring Security.
// So if we decide to remove Spring Security from our project, we can easily do it
// by simply changing the CurrentUser annotation-