package com.gruposuporte.projetosuporte.controllers;

import com.gruposuporte.projetosuporte.data.Call;
import com.gruposuporte.projetosuporte.repository.CallRepository;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import com.gruposuporte.projetosuporte.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final UserUtils userUtils;
    private CallRepository callRepository;

    @Autowired
    public HomeController(UserRepository userRepository, UserUtils userUtils, CallRepository callRepository) {
        this.userRepository = userRepository;
        this.userUtils = userUtils;
        this.callRepository = callRepository;
    }


    @GetMapping("/")
    public String index(Model model){
        var user = userUtils.getCurrentUser();
        var calls = callRepository.findAll(Sort.by(Sort.Direction.DESC,"data"));
        model.addAttribute("currentUser", user);
        model.addAttribute("calls", calls);
        return "index";
    }


}
