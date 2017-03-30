package ru.belyaeva.rsoi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.belyaeva.rsoi.service.LoginService;
import ru.belyaeva.rsoi.web.model.User;

/**
 * Created by user on 13.11.2016.
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    LoginService loginService;


    @GetMapping("/getIndex")
    @ResponseBody
    public ModelAndView getIndex(Model model) {
        model.addAttribute("user", new User());
        return new ModelAndView("index");
    }


    /*@RequestMapping(value = "/in", method = RequestMethod.POST)
    public ModelAndView auth(@ModelAttribute UserEntity user) {
        ModelAndView nmav = new ModelAndView("profilePage");
        nmav.addObject("name", user.getUsername());
        nmav.addObject("email", user.getEmail());
        return nmav;
    }*/

    @RequestMapping(value = "/check-auth", method = RequestMethod.POST)
    public ModelAndView login(@ModelAttribute User user) {
        // проверка пароля
        User fullInfoUser = loginService.checkAuthAndGetInfo(user);

            ModelAndView nmav = new ModelAndView("profile");
            nmav.addObject("name", fullInfoUser.getFirstNameReal());
            nmav.addObject("email", fullInfoUser.getEmail());
            return nmav;
    }

    @GetMapping("/getRegister")
    public ModelAndView getRegister(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("okMessage", false);
        model.addAttribute("errorMessage", false);
        return new ModelAndView("registerPage");
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registration(@ModelAttribute User user, Model model) {

        if (loginService.register(user)) {
            model.addAttribute("okMessage", true);
            model.addAttribute("errorMessage", false);
            model.addAttribute("clientId", user.getClientId());
            model.addAttribute("clientSecret", user.getClientSecret());
            return new ModelAndView("resultRegPage");
        } else {
            model.addAttribute("errorMessage", true);
            model.addAttribute("okMessage", false);
        }
        model.addAttribute("user", new User());
        return new ModelAndView("index");
    }
}
