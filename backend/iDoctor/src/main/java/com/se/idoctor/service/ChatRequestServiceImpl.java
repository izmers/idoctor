package com.se.idoctor.service;

import com.se.idoctor.dto.ChatRequestDto;
import com.se.idoctor.entity.*;
import com.se.idoctor.exception.ChatRequestAlreadyExistsException;
import com.se.idoctor.exception.EntityNotFoundException;
import com.se.idoctor.repository.ChatChannelRepository;
import com.se.idoctor.repository.ChatRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ChatRequestServiceImpl implements ChatRequestService {
    private ChatRequestRepository chatRequestRepository;
    private UserService userService;
    private DoctorService doctorService;
    private ChatChannelRepository chatChannelRepository;

    @Override
    public ChatRequest getChatRequestById(Long id) {
        return this.chatRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ChatRequest.class, id));
    }

    @Override
    public List<ChatRequest> getChatRequestsByDoctorUsername(String username) {
        return this.chatRequestRepository.findByDoctorUsernameAndChatRequestStatus(username, ChatRequestStatus.PENDING);
    }

    @Override
    public ChatRequest createChatRequestWithDto(ChatRequestDto chatRequestDto) {
        Optional<ChatRequest> existingChatRequest = this.chatRequestRepository.findChatRequestByDoctorUserUsernameAndUserUsername(chatRequestDto.getDoctorUsername(), chatRequestDto.getUserUsername());

        if (existingChatRequest.isPresent()) {
            Optional<ChatChannel> chatChannel = this.chatChannelRepository.findChatChannelByDoctorUserUsernameAndPatientUsername(chatRequestDto.getDoctorUsername(), chatRequestDto.getUserUsername());
            if (chatChannel.isPresent() && chatChannel.get().getChatChannelStatus() == ChatChannelStatus.INACTIVE) {
                ChatRequest chatRequest = existingChatRequest.get();
                chatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);
                chatRequest.setUserNote(chatRequestDto.getUserNote());
                return this.chatRequestRepository.save(chatRequest);
            }
            throw new ChatRequestAlreadyExistsException(chatRequestDto.getDoctorUsername(), chatRequestDto.getUserUsername());
        }

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);
        chatRequest.setUserNote(chatRequestDto.getUserNote());
        chatRequest.setUser(this.userService.getUserByUsernameOrEmail(chatRequestDto.getUserUsername(), chatRequestDto.getUserUsername()));
        chatRequest.setDoctor(this.userService.getUserByUsernameOrEmail(chatRequestDto.getDoctorUsername(), chatRequestDto.getDoctorUsername()).getDoctor());

        return this.chatRequestRepository.save(chatRequest);
    }

    @Override
    public ChatRequest createChatRequest(ChatRequest chatRequest, Long doctorId) {
        Userx user = this.userService.getCurrentUser();
        chatRequest.setUser(user);
        chatRequest.setDoctor(this.doctorService.getDoctorById(doctorId));
        chatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);

        return this.chatRequestRepository.save(chatRequest);
    }

    private ChatChannel createChannel(ChatRequest chatRequest, ChatChannelStatus chatChannelStatus) {
        ChatChannel chatChannel = new ChatChannel();
        chatChannel.setChatChannelStatus(chatChannelStatus);
        chatChannel.setDoctor(chatRequest.getDoctor());
        chatChannel.setPatient(chatRequest.getUser());
        return chatChannel;
    }

    @Override
    public ChatRequest acceptChatRequest(ChatRequestDto chatRequestDto, Long id) {
        ChatRequest chatRequest = this.getChatRequestById(id);
        chatRequest.setDoctorNote(chatRequestDto.getDoctorNote());
        chatRequest.setChatRequestStatus(ChatRequestStatus.ACCEPTED);

        Optional<ChatChannel> existingChatChannel = this.chatChannelRepository.findChatChannelByDoctorUserUsernameAndPatientUsername(chatRequestDto.getDoctorUsername(), chatRequestDto.getUserUsername());
        ChatChannel chatChannel;

        if (existingChatChannel.isPresent()) {
            chatChannel = existingChatChannel.get();
            chatChannel.setChatChannelStatus(ChatChannelStatus.ACTIVE);
        } else {
            chatChannel = createChannel(chatRequest, ChatChannelStatus.ACTIVE);
        }

        this.chatChannelRepository.save(chatChannel);
        return this.chatRequestRepository.save(chatRequest);
    }

    @Override
    public ChatRequest declineChatRequest(ChatRequestDto chatRequestDto, Long id) {
        ChatRequest chatRequest = this.getChatRequestById(id);
        chatRequest.setDoctorNote(chatRequestDto.getDoctorNote());
        chatRequest.setChatRequestStatus(ChatRequestStatus.DECLINED);

        Optional<ChatChannel> existingChatChannel = this.chatChannelRepository.findChatChannelByDoctorUserUsernameAndPatientUsername(chatRequestDto.getDoctorUsername(), chatRequestDto.getUserUsername());
        ChatChannel chatChannel;

        if (existingChatChannel.isPresent()) {
             chatChannel = existingChatChannel.get();
             chatChannel.setChatChannelStatus(ChatChannelStatus.INACTIVE);
             chatChannel.setDoctorNote(chatRequestDto.getDoctorNote());
        } else {
            chatChannel = createChannel(chatRequest, ChatChannelStatus.INACTIVE);
            chatChannel.setDoctorNote(chatRequestDto.getDoctorNote());
        }

        this.chatChannelRepository.save(chatChannel);
        return this.chatRequestRepository.save(chatRequest);
    }
}
