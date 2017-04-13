package ru.belyaeva.rsoi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.belyaeva.rsoi.web.model.UserInfoResponse;
import ru.belyaeva.rsoi.service.UserService;

/**
 * Created by user on 13.12.2016.
 */
@Controller
@RequestMapping("/")
public class UserInfoController {

    @Autowired
    UserService userInfoService;

    @RequestMapping(value = "me",
            method = RequestMethod.GET)
    @ResponseBody
    public UserInfoResponse getUserInfo(@RequestHeader(value = "Authorization") String token) {
        return userInfoService.getUserInfo(token);
    }

    @RequestMapping(value = "user/information",
            method = RequestMethod.POST)
    @ResponseBody
    public UserInfoResponse getUserInfo_inside(
            @RequestBody String token) {
        return userInfoService.getUserInfo(token);
    }
}
