package com.gruposuporte.projetosuporte.controllers;

import com.gruposuporte.projetosuporte.data.Call;
import com.gruposuporte.projetosuporte.data.Message;
import com.gruposuporte.projetosuporte.data.UserRole;
import com.gruposuporte.projetosuporte.dto.CallAgent;
import com.gruposuporte.projetosuporte.dto.CallRequest;
import com.gruposuporte.projetosuporte.dto.ChatMessage;
import com.gruposuporte.projetosuporte.repository.CallRepository;
import com.gruposuporte.projetosuporte.repository.ChatMessageRepository;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import com.gruposuporte.projetosuporte.services.CallService;
import com.gruposuporte.projetosuporte.utils.CreateCallValidator;
import com.gruposuporte.projetosuporte.utils.UserUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class CallController {
    private final CallService callService;
    private final UserRepository userRepository;

    private final CreateCallValidator callValidator;

    private final CallRepository callRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final UserUtils userUtils;

    @Autowired //instanciar a classe UserRepository
    public CallController(UserRepository userRepository, ChatMessageRepository chatMessageRepository, CreateCallValidator callValidator, CallRepository callRepository, UserUtils userUtils, CallService callService) {
        this.userRepository = userRepository;
        this.callValidator = callValidator;
        this.callRepository = callRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userUtils = userUtils;
        this.callService = callService;
    }

    @GetMapping("/realizar-call")
    public String createCall(@ModelAttribute("call") CallRequest callRequest, Model model) {
        model.addAttribute("currentUser", userUtils.getCurrentUser());

        return "realizar-call";
    }

    @GetMapping("/support-chat/{call-id}")
    public String supportChat(@PathVariable("call-id") UUID callId, @ModelAttribute("chat_message") ChatMessage chatMessage, Model model) {

        var callOptional = callRepository.findById(callId);
        if (callOptional.isEmpty()) {
            return "redirect:/";
        }
        var user = userUtils.getCurrentUser();
        var call = callOptional.get();
        var createdDate = userUtils.getFormattedDate(call.getData());
//        var createdDate = userUtils.getFormattedDate(call.getData());

        var messages = chatMessageRepository.getMessagesByCall(call)
                .stream().sorted(Comparator.comparing(Message::getDatetime)).toList();

        if (!call.getConsumer().getId().equals(user.getId()) && user.getRole() != UserRole.AGENT) {
            return "redirect:/";

        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentCall", call);
        model.addAttribute("createdDate", createdDate);
        model.addAttribute("messages", messages);
        model.addAttribute("userUtils", userUtils);
        return "support-chat-room";
    }

    @GetMapping("/my-support-requests")
    public String mySupportRequests(@RequestParam(name = "orderBy", defaultValue = "desc") String orderBy, Model model) {
        var user = userUtils.getCurrentUser();
        List<Call> calls;
        if (user.getRole().equals(UserRole.AGENT)) {
            calls = orderBy.equalsIgnoreCase("asc") ? callRepository.findAllByAgentOrderByDataAsc(user) : callRepository.findAllByAgentOrderByDataDesc(user);
        } else {
            calls = orderBy.equalsIgnoreCase("asc") ? callRepository.findAllByConsumerOrderByDataAsc(user) : callRepository.findAllByConsumerOrderByDataDesc(user);

        }
        model.addAttribute("currentUser", user);
        model.addAttribute("calls", calls);
        model.addAttribute("order", orderBy);

        return "my-support-requests";
    }


    @PostMapping("/create-call")
    public String createCall(@ModelAttribute("call") CallRequest callRequest, RedirectAttributes attributes, BindingResult bindingResult) throws IOException {
        callValidator.validate(callRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/realizar-call";
        }

        var user = userUtils.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        var call = new Call(new Date(), callRequest.title(), true, callRequest.description(), user);
        var file = callRequest.multipartFile();
        if (!file.isEmpty() && file.getOriginalFilename() != null) {
            callService.saveImageCall(user.getId().toString(), file);
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            call.setImage(fileName);
        }

        callRepository.save(call);
        attributes.addFlashAttribute("success", "Chamado criado com sucesso.");
        return "redirect:/support-chat/" + call.getId();
    }

    @Transactional
    @PostMapping("/closeCall/{callId}")
    public String closeCall(@PathVariable("callId") UUID uuid) {
        var call = callRepository.findById(uuid);
        var user = userUtils.getCurrentUser();
        if (call.isPresent() && user != null && user.getRole() == UserRole.AGENT) {

            callRepository.closeCall(call.get().getId());
            return "redirect:/support-chat/" + call.get().getId();
        }
        return "/login";
    }

    @PostMapping("/ligar/{callId}/{agentId}")
    public String ligar(@PathVariable("callId") UUID callId, @PathVariable("agentId") UUID agentId) {
        var callOptional = callRepository.findById(callId);
        var agent = userRepository.findById(agentId);
        if (!callOptional.isPresent() || !agent.isPresent()) {
            return "redirect:/";
        }
        if (agent.get().getRole() == UserRole.CONSUMER) {
            return "redirect:/";
        }

        if (callOptional.get().getAgent() != null) {
            return "redirect:/";
        }
        var call = callOptional.get();
        call.setAgent(agent.get());
        callRepository.save(call);

        if(agent.get().getRole() == UserRole.MANAGER){
            return "redirect:/";
        }else{
            return "redirect:/support-chat/" + call.getId();
        }
    }

    
    @PostMapping("/delete-call/{callId}")
    public String deleteCall(@PathVariable("callId") UUID callId){
        var callOptional = callRepository.findById(callId);
        var user = userUtils.getCurrentUser();
        if (!callOptional.isPresent()) {
            return "redirect:/";
        }
        if (user.getRole() != UserRole.CONSUMER) {
            return "redirect:/";
        }
        callRepository.delete(callOptional.get());
        return "redirect:/my-support-requests";
    }
}
