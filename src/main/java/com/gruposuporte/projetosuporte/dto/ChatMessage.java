package com.gruposuporte.projetosuporte.dto;

import java.util.Date;
import java.util.UUID;

public record ChatMessage(
        UUID userId,
        UUID callId,
        String sender,
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