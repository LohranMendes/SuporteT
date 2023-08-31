package com.gruposuporte.projetosuporte.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/agent") // metodo para acessar tela de cadastro do agente
    public String registerAgent() {
        return "agent";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
