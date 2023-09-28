package com.gruposuporte.projetosuporte.controllers;


import com.gruposuporte.projetosuporte.data.User;
import com.gruposuporte.projetosuporte.data.UserRole;
import com.gruposuporte.projetosuporte.dto.LoginRequest;
import com.gruposuporte.projetosuporte.dto.SignupRequest;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import com.gruposuporte.projetosuporte.services.SecurityService;
import com.gruposuporte.projetosuporte.utils.UserUtils;
import com.gruposuporte.projetosuporte.utils.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private final UserUtils userUtils;

    private SecurityService securityService;

    @Autowired
    public AuthenticationController(UserRepository userRepository, UserValidator userValidator, PasswordEncoder encoder, SecurityService securityService, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.securityService = securityService;
        this.userValidator = userValidator;
        this.userUtils = userUtils;
    }

    @GetMapping("/register")
    public String register(@ModelAttribute("user") SignupRequest signupRequest) {
        var user = userUtils.getCurrentUser();
        if (securityService.isAuthenticated() && user != null) {
                return "redirect:/";
        }
        return "register-user";
    }

    @GetMapping("/register-agent") // metodo para acessar tela de cadastro do agente
    public String registerAgent(@ModelAttribute("user") SignupRequest signupRequest) {
        if (securityService.isAuthenticated()) {
            return "redirect:/";
        }
        return "register-agent";
    }

    @GetMapping("/login")
    public String login(@ModelAttribute("user") LoginRequest loginRequest, Model model, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return "redirect:/";
        }

        if (error != null)
            model.addAttribute("login_error", "Seu nome de usuário e/ou senha são inválidos.");

        if (logout != null)
            model.addAttribute("message", "Você foi desconectado com sucesso.");

        return "login";
    }


    @PostMapping("/register-user")
    public String registerUser(@ModelAttribute("user") SignupRequest signupRequest, RedirectAttributes attributes, BindingResult bindingResult) {
        userValidator.validate(signupRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            return "register-user";
        }

        register(signupRequest, UserRole.CONSUMER);

        attributes.addFlashAttribute("success", "Você foi cadastrado com sucesso!");
        return "redirect:/login";

    }

    @PostMapping("/register-agent")
    public String registerAgent(@ModelAttribute("user") SignupRequest signupRequest, RedirectAttributes attributes, BindingResult bindingResult) {
        userValidator.validate(signupRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            return "register-agent";
        }

        register(signupRequest, UserRole.AGENT);

        attributes.addFlashAttribute("success", "Você foi cadastrado com sucesso!");
        return "redirect:/login";

    }

    private void register(SignupRequest signupRequest, UserRole role) {
        User user = new User(signupRequest.name(), encoder.encode(signupRequest.password()), role, signupRequest.email(), signupRequest.username());
        userRepository.save(user);
        securityService.autoLogin(signupRequest.username(), signupRequest.password());
    }

}
