package com.se.idoctor;

import com.se.idoctor.dto.ChatTextDto;
import com.se.idoctor.entity.ChatChannel;
import com.se.idoctor.entity.ChatText;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.repository.ChatChannelRepository;
import com.se.idoctor.repository.ChatTextRepository;
import com.se.idoctor.service.ChatChannelService;
import com.se.idoctor.service.ChatTextServiceImpl;
import com.se.idoctor.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
class ChatTextServiceImplTests {

    @Mock
    private ChatTextRepository chatTextRepository;

    @Mock
    private ChatChannelService chatChannelService;

    @Mock
    private ChatChannelRepository chatChannelRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatTextServiceImpl chatTextService;

    @Test
    void testFindChatTextsByChatChannelIdOrderedByTimestamp_Success() {
        Long channelId = 1L;
        ChatText chatText1 = new ChatText();
        chatText1.setId(100L);
        chatText1.setTimestamp(new Date(1706500200000L));

        ChatText chatText2 = new ChatText();
        chatText2.setId(101L);
        chatText2.setTimestamp(new Date(1706500800000L));

        List<ChatText> mockChatTexts = Arrays.asList(chatText1, chatText2);

        when(chatTextRepository.findChatTextsByChannelIdOrderByTimestamp(channelId)).thenReturn(mockChatTexts);

        List<ChatText> result = chatTextService.findChatTextsByChatChannelIdOrderedByTimestamp(channelId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100L, result.get(0).getId());
        assertEquals(101L, result.get(1).getId());

        verify(chatTextRepository, times(1)).findChatTextsByChannelIdOrderByTimestamp(channelId);
    }

    @Test
    void testSaveChatText_Success() {
        ChatTextDto chatTextDto = new ChatTextDto();
        chatTextDto.setChannelId(1L);
        chatTextDto.setContent("Hello, this is a test message.");
        chatTextDto.setSenderCred("john.doe");
        chatTextDto.setRecipientCred("doctor.jane");
        chatTextDto.setCreated(new Date());

        ChatChannel chatChannel = new ChatChannel();
        chatChannel.setId(1L);

        Userx sender = new Userx();
        sender.setUsername("john.doe");

        Userx recipient = new Userx();
        recipient.setUsername("doctor.jane");

        ChatText chatText = new ChatText();
        chatText.setContent(chatTextDto.getContent());
        chatText.setTimestamp(chatTextDto.getCreated());
        chatText.setChatChannel(chatChannel);
        chatText.setSender(sender);
        chatText.setRecipient(recipient);

        when(chatChannelService.findById(chatTextDto.getChannelId())).thenReturn(chatChannel);
        when(userService.getUserByUsernameOrEmail(chatTextDto.getSenderCred(), chatTextDto.getSenderCred()))
                .thenReturn(sender);
        when(userService.getUserByUsernameOrEmail(chatTextDto.getRecipientCred(), chatTextDto.getRecipientCred()))
                .thenReturn(recipient);
        when(chatChannelRepository.save(chatChannel)).thenReturn(chatChannel);
        when(chatTextRepository.save(any(ChatText.class))).thenReturn(chatText);

        ChatText result = chatTextService.save(chatTextDto);

        assertNotNull(result);
        assertEquals("Hello, this is a test message.", result.getContent());
        assertEquals(sender, result.getSender());
        assertEquals(recipient, result.getRecipient());
        assertEquals(chatChannel, result.getChatChannel());

        verify(chatChannelService, times(1)).findById(chatTextDto.getChannelId());
        verify(userService, times(1)).getUserByUsernameOrEmail(chatTextDto.getSenderCred(), chatTextDto.getSenderCred());
        verify(userService, times(1)).getUserByUsernameOrEmail(chatTextDto.getRecipientCred(), chatTextDto.getRecipientCred());
        verify(chatChannelRepository, times(1)).save(chatChannel);
        verify(chatTextRepository, times(1)).save(any(ChatText.class));
    }
}
