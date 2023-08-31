package com.gruposuporte.projetosuporte.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthenticationController {

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/register/agent") // metodo para acessar tela de cadastro do agente
    public String registerAgent() {
        return "register";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

}