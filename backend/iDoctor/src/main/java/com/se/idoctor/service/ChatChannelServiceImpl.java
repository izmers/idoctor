package com.se.idoctor.service;

import com.se.idoctor.entity.ChatChannel;
import com.se.idoctor.exception.EntityNotFoundException;
import com.se.idoctor.repository.ChatChannelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ChatChannelServiceImpl implements ChatChannelService {
    private ChatChannelRepository chatChannelRepository;

    @Override
    public ChatChannel findById(Long id) {
        return this.chatChannelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ChatChannel.class, id));
    }

    @Override
    public List<ChatChannel> findChatChannelsByUserUsernameOrEmail(String cred) {
        return this.chatChannelRepository.findByPatientUsernameOrPatientEmail(cred, cred);
    }

    @Override
    public List<ChatChannel> findChatChannelsByDoctorUsernameOrEmail(String cred) {
        return this.chatChannelRepository.findByDoctorUserUsernameOrDoctorUserEmail(cred, cred);
    }
}
