package com.gruposuporte.projetosuporte.controllers;

import com.gruposuporte.projetosuporte.data.Call;
import com.gruposuporte.projetosuporte.data.Message;
import com.gruposuporte.projetosuporte.data.UserRole;
import com.gruposuporte.projetosuporte.dto.CallRequest;
import com.gruposuporte.projetosuporte.dto.ChatMessage;
import com.gruposuporte.projetosuporte.repository.CallRepository;
import com.gruposuporte.projetosuporte.repository.ChatMessageRepository;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import com.gruposuporte.projetosuporte.utils.CreateCallValidator;
import com.gruposuporte.projetosuporte.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

@Controller
public class CallController {
    private final UserRepository userRepository;

    private final CreateCallValidator callValidator;

    private final CallRepository callRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final UserUtils userUtils;

    @Autowired //instanciar a classe UserRepository
    public CallController(UserRepository userRepository, ChatMessageRepository chatMessageRepository, CreateCallValidator callValidator, CallRepository callRepository, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.callValidator = callValidator;
        this.callRepository = callRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userUtils = userUtils;
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
//            return "redirect:/page-error-404";
        }
        var user = userUtils.getCurrentUser();
        var call = callOptional.get();
        var createdDate = userUtils.getFormattedDate(call.getData());
//        var createdDate = userUtils.getFormattedDate(call.getData());

        var messages = chatMessageRepository.getMessagesByCall(call)
                .stream().sorted(Comparator.comparing(Message::getDatetime)).toList();

        if (!call.getCostumer().getId().equals(user.getId()) && user.getRole() != UserRole.AGENT) {
            return "redirect:/";
            // REDIRECIONA PARA UMA TELA QUE INFORA QUE A CHAMDA NÃO O PERTENCE
//            return "redirect:/call-forbidden";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentCall", call);
        model.addAttribute("createdDate", createdDate);
        model.addAttribute("messages", messages);
        model.addAttribute("userUtils", userUtils);
        return "support-chat-room";
    }


    @PostMapping("/create-call")
    public String createCall(@ModelAttribute("call") CallRequest callRequest, RedirectAttributes attributes, BindingResult bindingResult) {
        callValidator.validate(callRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/realizar-call";
        }

        var user = userUtils.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        var call = new Call(new Date(), callRequest.title(), true, callRequest.description(), user);

        callRepository.save(call);
        attributes.addFlashAttribute("success", "Chamado criado com sucesso.");
        return "redirect:/support-chat/" + call.getId();
    }


}
