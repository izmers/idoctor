package com.se.idoctor.web;

import com.se.idoctor.entity.ChatChannel;
import com.se.idoctor.service.ChatChannelService;
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
@RequestMapping("/api/chat-channel")
public class ChatChannelController {
    private ChatChannelService chatChannelService;

    @GetMapping("/by-patient/{cred}")
    public ResponseEntity<List<ChatChannel>> getChatChannelsByUser(@PathVariable String cred) {
        return new ResponseEntity<>(this.chatChannelService.findChatChannelsByUserUsernameOrEmail(cred), HttpStatus.OK);
    }

    @GetMapping("/by-doctor/{cred}")
    public ResponseEntity<List<ChatChannel>> getChatChannelsByDoctor(@PathVariable String cred) {
        return new ResponseEntity<>(this.chatChannelService.findChatChannelsByDoctorUsernameOrEmail(cred), HttpStatus.OK);
    }
}
