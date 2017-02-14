package me.cyril.mongodb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by qianmin on 2017/2/13.
 */
@RestController
public class HelloWorldController {

    @RequestMapping("/")
    public String home(){
        return "Hello MongoDB!";
    }
}
