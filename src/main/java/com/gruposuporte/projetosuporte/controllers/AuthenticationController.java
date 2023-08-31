package com.gruposuporte.projetosuporte.controllers;


import com.gruposuporte.projetosuporte.repository.UserRepository;
import com.gruposuporte.projetosuporte.security.UserValidator;
import com.gruposuporte.projetosuporte.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    private final UserValidator userValidator;

    private SecurityService securityService;

    @Autowired
    public AuthenticationController(UserRepository userRepository, UserValidator userValidator, PasswordEncoder encoder, SecurityService securityService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.securityService = securityService;
        this.userValidator = userValidator;
    }

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
