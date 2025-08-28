package com.test.first.tut.hello.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Spring Boot!";
    }
    
    
    @GetMapping("/welcome")
    public String welcomeMessage() {
        return "Welcome to your backend journey!";
    }

}
