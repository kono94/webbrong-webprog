package de.hsbhv.gamegateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EurekaController {

    @GetMapping("/test")
    public String getMessage() {
        return "Hello World";
    }
}
