package com.gruposuporte.projetosuporte.controllers;

import com.gruposuporte.projetosuporte.data.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @GetMapping("/registro")
    public String exibirFormularioRegistro(Model model) {
        model.addAttribute("usuario", new User()); // 'Usuario' é a classe do modelo de usuário
        return "formulario-registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute User usuario) {
        // Lógica para salvar o usuário no banco de dados
        return "redirect:/registro?sucesso";
    }
}
