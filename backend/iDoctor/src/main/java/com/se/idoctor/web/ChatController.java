package com.se.idoctor.web;

import com.se.idoctor.dto.ChatRequestDto;
import com.se.idoctor.dto.ChatTextDto;
import com.se.idoctor.entity.ChatRequest;
import com.se.idoctor.entity.ChatText;
import com.se.idoctor.exception.ChatRequestAlreadyExistsException;
import com.se.idoctor.service.ChatRequestService;
import com.se.idoctor.service.ChatTextService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatTextService chatTextService;
    private final ChatRequestService chatRequestService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatTextDto chatTextDto) {
        ChatText msg = chatTextService.save(chatTextDto);
        messagingTemplate.convertAndSendToUser(msg.getRecipient().getUsername(), "/queue/messages", chatTextDto);
    }

    @MessageMapping("/request")
    public void processRequest(@Payload ChatRequestDto chatRequestDto) {
        try {
            ChatRequest chatRequest = chatRequestService.createChatRequestWithDto(chatRequestDto);
            chatRequestDto.setId(chatRequest.getId());
            messagingTemplate.convertAndSendToUser(chatRequestDto.getDoctorUsername(), "/queue/requests", chatRequestDto);
        } catch (ChatRequestAlreadyExistsException e) {
            messagingTemplate.convertAndSendToUser(chatRequestDto.getUserUsername(), "/queue/errors", e.getMessage());
        }

    }
}
