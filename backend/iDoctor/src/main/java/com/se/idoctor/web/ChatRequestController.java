package com.se.idoctor.web;

import com.se.idoctor.dto.ChatRequestDto;
import com.se.idoctor.entity.ChatRequest;
import com.se.idoctor.service.ChatRequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-request")
@AllArgsConstructor
public class ChatRequestController {
    private ChatRequestService chatRequestService;

    @GetMapping("/{id}")
    public ResponseEntity<ChatRequest> getChatRequestById(@PathVariable Long id) {
        return new ResponseEntity<>(this.chatRequestService.getChatRequestById(id), HttpStatus.OK);
    }

    @GetMapping("/by-doctor/{username}")
    public ResponseEntity<List<ChatRequest>> getChatRequestsByDoctorUsername(@PathVariable String username) {
        return new ResponseEntity<>(this.chatRequestService.getChatRequestsByDoctorUsername(username), HttpStatus.OK);
    }

    @PostMapping("/create/{doctorId}")
    public ResponseEntity<ChatRequest> createChatRequest(@Valid ChatRequest chatRequest, @PathVariable Long doctorId) {
        return new ResponseEntity<>(this.chatRequestService.createChatRequest(chatRequest, doctorId), HttpStatus.CREATED);
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<ChatRequest> acceptChatRequest(@Valid @RequestBody ChatRequestDto chatRequestDto, @PathVariable Long id) {
        return new ResponseEntity<>(this.chatRequestService.acceptChatRequest(chatRequestDto, id), HttpStatus.OK);
    }

    @PutMapping("/decline/{id}")
    public ResponseEntity<ChatRequest> declineChatRequest(@Valid @RequestBody ChatRequestDto chatRequestDto, @PathVariable Long id) {
        return new ResponseEntity<>(this.chatRequestService.declineChatRequest(chatRequestDto, id), HttpStatus.OK);
    }
}
