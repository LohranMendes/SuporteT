package com.gruposuporte.projetosuporte.controllers;

import com.gruposuporte.projetosuporte.repository.UserRepository;
import com.gruposuporte.projetosuporte.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final UserUtils userUtils;


    @Autowired
    public HomeController(UserRepository userRepository, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.userUtils = userUtils;
    }


    @GetMapping("/")
    public String index(Model model){
        var user = userUtils.getCurrentUser();
        model.addAttribute("currentUser", user);
        return "index";
    }

}
