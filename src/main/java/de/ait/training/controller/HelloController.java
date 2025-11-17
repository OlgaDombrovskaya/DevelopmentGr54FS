package de.ait.training.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    private final String  helloMessage;

    public HelloController(@Value("${messages.hello}") String helloMessage) {
        this.helloMessage = helloMessage;
    }
@GetMapping
    public String hello() {
        return helloMessage;
    }
}
