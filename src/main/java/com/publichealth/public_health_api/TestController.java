package com.publichealth.public_health_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping ("/hello")
    public String hello(){
        return "Hello world";
    }

}
