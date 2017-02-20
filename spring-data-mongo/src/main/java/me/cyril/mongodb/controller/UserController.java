package me.cyril.mongodb.controller;

import me.cyril.mongodb.repository.model.User;
import me.cyril.mongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by qianmin on 2017/2/17.
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/user/save", method = RequestMethod.POST,
            consumes="application/json",produces="application/json")
    public String save(@RequestBody User user){
        userService.save(user);
        return "ok";
    }
}
