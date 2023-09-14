package com.gruposuporte.projetosuporte.controllers;


import com.gruposuporte.projetosuporte.data.Message;
import com.gruposuporte.projetosuporte.dto.ChatMessage;
import com.gruposuporte.projetosuporte.repository.CallRepository;
import com.gruposuporte.projetosuporte.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/calls")
public class MessageContoller {

    private ChatMessageRepository chatMessageRepository;
    private CallRepository callRepository;


    @Autowired
    public MessageContoller(ChatMessageRepository chatMessageRepository, CallRepository callRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.callRepository = callRepository;
    }

    @GetMapping("/{callId}/messages")
    public List<ChatMessage> getAllMessagesFromCall(@PathVariable UUID callId) {
        var callOptional = callRepository.findById(callId);
        var call = callOptional.orElse(null);
        return chatMessageRepository.getMessagesByCall(call)
                .stream()
                .sorted(Comparator.comparing(Message::getDatetime))
                .map(message -> new ChatMessage(message.getId(), message.getCall().getId(), message.getUser().getName(), message.getText(), message.getDatetime())).toList();
    }


}
