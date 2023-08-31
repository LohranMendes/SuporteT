package com.gruposuporte.projetosuporte.controllers;


import com.gruposuporte.projetosuporte.data.User;
import com.gruposuporte.projetosuporte.data.UserRole;
import com.gruposuporte.projetosuporte.dto.SignupRequest;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import com.gruposuporte.projetosuporte.security.UserValidator;
import com.gruposuporte.projetosuporte.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String register(@ModelAttribute("user") SignupRequest signupRequest) {
        if (securityService.isAuthenticated()) {
            return "redirect:/";
        }
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


    @PostMapping("/register-user")
    public String registerUser(@ModelAttribute("user") SignupRequest signupRequest, RedirectAttributes attributes, BindingResult bindingResult){

        userValidator.validate(signupRequest, bindingResult);
        System.out.println("AuthenticationController.registerUser chegou aqui "+bindingResult.hasErrors());

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = new User(signupRequest.name(), encoder.encode(signupRequest.password()), UserRole.CONSUMER, signupRequest.email(), signupRequest.username());

        userRepository.save(user);

        securityService.autoLogin(signupRequest.username(), signupRequest.password());

        attributes.addFlashAttribute("success", "Você foi cadastrado com sucesso!");
        return "redirect:/";

    }

}
