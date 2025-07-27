package com.se.idoctor.service;

import com.se.idoctor.dto.ChatRequestDto;
import com.se.idoctor.entity.ChatRequest;

import java.util.List;

public interface ChatRequestService {
    ChatRequest getChatRequestById(Long id);
    List<ChatRequest> getChatRequestsByDoctorUsername(String username);
    ChatRequest createChatRequest(ChatRequest chatRequest, Long doctorId);
    ChatRequest acceptChatRequest(ChatRequestDto chatRequestDto, Long id);
    ChatRequest createChatRequestWithDto(ChatRequestDto chatRequestDto);
    ChatRequest declineChatRequest(ChatRequestDto chatRequestDto, Long id);
}
