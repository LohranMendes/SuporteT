package com.gruposuporte.projetosuporte.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.gruposuporte.projetosuporte.repository.CallRepository;
import com.gruposuporte.projetosuporte.repository.UserRepository;

import com.gruposuporte.projetosuporte.dto.Relatorio;

@Controller
public class RelatorioController {
    private CallRepository callRepository;
    private UserRepository userRepository;
    
    @Autowired
    public RelatorioController (CallRepository callRepository, UserRepository userRepository){
        this.callRepository = callRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/csv-relatorio")
    public String dados() {
       var chamadas = callRepository.findAll();

       var relatorios = chamadas.stream().map(chamado -> {
        var agente = chamado.getAgent();
        if(agente != null){
        var chamadasDoUser = chamadas.stream().filter(c -> c.getAgent() != null && c.getAgent().getId().equals(agente.getId())).toList();
        String nome = agente.getName();
        int abertas = chamadasDoUser.stream().filter(c -> c.isStatus()).toList().size();
        int fechadas = chamadasDoUser.size() - abertas;
        var relatorio = new Relatorio(nome, abertas,fechadas, chamadasDoUser.size());
        return relatorio;
        }

        return null;
       }).toList();

       System.out.println("Lista == "+relatorios);

        return "redirect:/";
    }
}
