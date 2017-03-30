package ru.belyaeva.rsoi.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.belyaeva.rsoi.web.model.User;


@Controller
@RequestMapping("/")
public class ViewController {

    @GetMapping("/")
    public  ModelAndView index(Model model) {

        model.addAttribute("user", new User());

        return new ModelAndView("index");
    }
}
