package com.gruposuporte.projetosuporte.controllers;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.gruposuporte.projetosuporte.repository.CallRepository;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import com.gruposuporte.projetosuporte.utils.UserUtils;
import com.opencsv.CSVWriter;

import jakarta.servlet.http.HttpServletResponse;

import com.gruposuporte.projetosuporte.data.UserRole;
import com.gruposuporte.projetosuporte.dto.Relatorio;

@Controller
public class RelatorioController {
    private CallRepository callRepository;
    private UserRepository userRepository;
    private final UserUtils userUtils;
    
    @Autowired
    public RelatorioController (CallRepository callRepository, UserRepository userRepository, UserUtils userUtils){
        this.callRepository = callRepository;
        this.userRepository = userRepository;
        this.userUtils = userUtils;
    }

    @GetMapping("/csv-relatorio")
    public String dados(HttpServletResponse response) {
        var user = userUtils.getCurrentUser();
        if(user.getRole() != UserRole.MANAGER){
            return "redirect:/";
        }

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
       }).distinct().toList();

       response.setContentType("text/csv");
       response.setHeader("Content-Disposition", "attachment; filename=Relatorio.csv");
       response.setCharacterEncoding("UTF-8");

       try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            // Escreve o cabeçalho do CSV
            writer.writeNext(new String[]{"Técnicos", "Abertas", "Fechadas", "Total"});

            for(Relatorio r : relatorios){
                if (r != null)
                    writer.writeNext(new String[]{r.tecnico(), String.valueOf(r.abertas()), String.valueOf(r.fechadas()), String.valueOf(r.total())});
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }
}
