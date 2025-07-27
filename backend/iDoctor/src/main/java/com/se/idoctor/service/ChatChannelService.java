package com.se.idoctor.service;

import com.se.idoctor.entity.ChatChannel;

import java.util.List;

public interface ChatChannelService {
    ChatChannel findById(Long id);
    List<ChatChannel> findChatChannelsByUserUsernameOrEmail(String cred);
    List<ChatChannel> findChatChannelsByDoctorUsernameOrEmail(String cred);
}
