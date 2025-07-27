package com.se.idoctor.service;


import com.se.idoctor.dto.ChatTextDto;
import com.se.idoctor.entity.ChatText;

import java.util.List;

public interface ChatTextService {
    List<ChatText> findChatTextsByChatChannelIdOrderedByTimestamp(Long channelId);
    ChatText save(ChatTextDto chatTextDto);
}
