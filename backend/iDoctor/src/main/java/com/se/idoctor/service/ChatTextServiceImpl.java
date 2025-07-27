package com.se.idoctor.service;

import com.se.idoctor.dto.ChatTextDto;
import com.se.idoctor.entity.ChatChannel;
import com.se.idoctor.entity.ChatText;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.repository.ChatChannelRepository;
import com.se.idoctor.repository.ChatTextRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ChatTextServiceImpl implements ChatTextService {
    private ChatTextRepository chatTextRepository;
    private ChatChannelService chatChannelService;
    private ChatChannelRepository chatChannelRepository;
    private UserService userService;

    @Override
    public List<ChatText> findChatTextsByChatChannelIdOrderedByTimestamp(Long channelId) {
        return this.chatTextRepository.findChatTextsByChannelIdOrderByTimestamp(channelId);
    }

    @Override
    public ChatText save(ChatTextDto chatTextDto) {
        ChatText chatText = new ChatText();
        ChatChannel chatChannel = this.chatChannelService.findById(chatTextDto.getChannelId());
        chatChannel.setLastMessage(chatTextDto.getContent());
        chatChannel.setDateOfLastMessage(chatTextDto.getCreated());
        Userx sender = this.userService.getUserByUsernameOrEmail(chatTextDto.getSenderCred(), chatTextDto.getSenderCred());
        Userx recipient = this.userService.getUserByUsernameOrEmail(chatTextDto.getRecipientCred(), chatTextDto.getRecipientCred());
        chatText.setContent(chatTextDto.getContent());
        chatText.setTimestamp(chatTextDto.getCreated());
        chatText.setChatChannel(chatChannel);
        chatText.setSender(sender);
        chatText.setRecipient(recipient);


        this.chatChannelRepository.save(chatChannel);
        return this.chatTextRepository.save(chatText);
    }
}
