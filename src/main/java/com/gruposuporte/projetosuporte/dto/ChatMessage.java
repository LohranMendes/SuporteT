package com.gruposuporte.projetosuporte.dto;

import java.util.Date;
import java.util.UUID;

public record ChatMessage(
        UUID userId,
        UUID callId,
        UUID agentId,
        String sender,
        String callTitle,
        String content,
        Date date
) {
}
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class ChatMessage {
//    private UUID userId;
//    private UUID callId;
//    private String content;
//
//
//}