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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Controller
public class CallController {
    private final UserRepository userRepository;

    private final CreateCallValidator callValidator;

    private final CallRepository callRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final UserUtils userUtils;

    @Value("${file.upload-dir}")
    private String uploadDir;

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
//        var calls = callRepository.findAll(Sort.by(orderBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "data"));

        var calls = orderBy.equalsIgnoreCase("asc") ? callRepository.findAllByConsumerOrderByDataAsc(user) : callRepository.findAllByConsumerOrderByDataDesc(user);

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
        if (!callRequest.multipartFile().isEmpty()) {
            try {
                String uploadDir = this.uploadDir + call.getConsumer().getUsername() + "/";
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();//cria diretorios
                }
                String fileName = callRequest.multipartFile().getOriginalFilename();
                if (fileName != null) {
                    String filePath = uploadDir + "/" + new SimpleDateFormat("YYYYMMdd_HHmmss_SSS", Locale.US).format(new Date()) + fileName.substring(fileName.lastIndexOf("."));
                    File destFile = new File(filePath);
                    if (!destFile.exists()) {
                        destFile.mkdirs();
                    }
                    callRequest.multipartFile().transferTo(destFile);
                    call.setImage("/uploads/" + user.getUsername() + "/" + destFile.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

}
