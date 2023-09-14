package com.gruposuporte.projetosuporte.controllers;


import com.gruposuporte.projetosuporte.data.Call;
import com.gruposuporte.projetosuporte.data.Message;
import com.gruposuporte.projetosuporte.data.User;
import com.gruposuporte.projetosuporte.dto.ChatMessage;
import com.gruposuporte.projetosuporte.repository.CallRepository;
import com.gruposuporte.projetosuporte.repository.ChatMessageRepository;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import com.gruposuporte.projetosuporte.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatContoller {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageRepository messageRepository;

    private final CallRepository callRepository;
    private final UserRepository userRepository;

    private final UserUtils userUtils;

    @Autowired
    public ChatContoller(SimpMessagingTemplate messagingTemplate, ChatMessageRepository messageRepository, CallRepository callRepository, UserRepository userRepository, UserUtils userUtils) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.callRepository = callRepository;
        this.userUtils = userUtils;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/allMessages")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {

//        var user = userUtils.getCurrentUser();
        var user = userRepository.findById(chatMessage.userId());
        var call = callRepository.findById(chatMessage.callId());

        if (user.isEmpty()) {
            System.out.println("ChatContoller.sendMessage error user is null id = " + chatMessage.userId());
            return chatMessage;
        }

        if (call.isEmpty()) {
            System.out.println("ChatContoller.sendMessage error call is null id = " + chatMessage.callId());
            return chatMessage;
        }

        User realUser = user.get();
        Call realCall = call.get();


        System.out.println("ChatContoller.sendMessage realCall " + realCall.getAgents());

//        if (realUser.getRole() == UserRole.AGENT){
////           if (realCall.getAgent().isEmpty() || realCall.getAgent().stream().filter(c -> !c.getRole().equals(realUser.getRole())).toList().isEmpty()){
//           if (realCall.getAgent().isEmpty()){
//               System.out.println("ChatContoller.sendMessage usurario "+realCall.getAgent());
//           }
//        }

//        if (realUser.getRole() == UserRole.AGENT) {
//            boolean agentExists = realCall.getAgents().stream()
//                    .anyMatch(existingAgent -> existingAgent.getId().equals(realUser.getId()));
//            if (!agentExists) {
//                realCall.getAgents().add(realUser);
//                System.out.println("ChatContoller.sendMessage added agent");
//            }else {
//                System.out.println("ChatContoller.sendMessage alread agent");
//            }
//        }


        var message = new Message(chatMessage.content(), chatMessage.date(), user.get(), call.get());
        messageRepository.save(message);

//        var messages = messageRepository.getMessagesByCall(call.get());

//        messagingTemplate.convertAndSend("/topic/public", chatMessage);
//        messagingTemplate.convertAndSend("/topic/allMessages", chatMessage);
        return chatMessage;
    }

}

