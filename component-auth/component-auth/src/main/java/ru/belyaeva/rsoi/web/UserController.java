package ru.belyaeva.rsoi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.belyaeva.rsoi.web.model.UserInfo;
import ru.belyaeva.rsoi.service.UserService;

/**
 * Created by user on 13.12.2016.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public UserInfo addUser(@RequestBody UserInfo user) {
        if (user == null){
            throw new IllegalArgumentException("User info can`t be null");
        }
        return userService.create(user);
    }
}
