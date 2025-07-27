package com.se.idoctor.web;

import com.se.idoctor.entity.ChatText;
import com.se.idoctor.service.ChatTextService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chat-text")
public class ChatTextController {
    private ChatTextService chatTextService;

    @GetMapping("/by-channel/{channelId}")
    public ResponseEntity<List<ChatText>> getChatTextsByChannel(@PathVariable Long channelId) {
        return new ResponseEntity<>(this.chatTextService.findChatTextsByChatChannelIdOrderedByTimestamp(channelId), HttpStatus.OK);
    }
}
