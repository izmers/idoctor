package com.se.idoctor;

import com.se.idoctor.entity.ChatChannel;
import com.se.idoctor.exception.EntityNotFoundException;
import com.se.idoctor.repository.ChatChannelRepository;
import com.se.idoctor.service.ChatChannelServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
class ChatChannelServiceImplTests {

    @Mock
    private ChatChannelRepository chatChannelRepository;

    @InjectMocks
    private ChatChannelServiceImpl chatChannelService;

    @Test
    void testFindById_Success() {
        Long channelId = 1L;
        ChatChannel mockChannel = new ChatChannel();
        mockChannel.setId(channelId);

        when(chatChannelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));

        ChatChannel result = chatChannelService.findById(channelId);

        assertNotNull(result);
        assertEquals(channelId, result.getId());

        verify(chatChannelRepository, times(1)).findById(channelId);
    }

    @Test
    void testFindById_NotFound() {
        Long channelId = 2L;
        when(chatChannelRepository.findById(channelId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> chatChannelService.findById(channelId));

        verify(chatChannelRepository, times(1)).findById(channelId);
    }

    @Test
    void testFindChatChannelsByUserUsernameOrEmail_Success() {
        String credential = "john.doe@example.com";
        ChatChannel chat1 = new ChatChannel();
        chat1.setId(1L);
        ChatChannel chat2 = new ChatChannel();
        chat2.setId(2L);

        List<ChatChannel> mockChannels = Arrays.asList(chat1, chat2);

        when(chatChannelRepository.findByPatientUsernameOrPatientEmail(credential, credential))
                .thenReturn(mockChannels);

        List<ChatChannel> result = chatChannelService.findChatChannelsByUserUsernameOrEmail(credential);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(chatChannelRepository, times(1))
                .findByPatientUsernameOrPatientEmail(credential, credential);
    }

    @Test
    void testFindChatChannelsByDoctorUsernameOrEmail_Success() {
        String credential = "doctor.jane@example.com";
        ChatChannel chat1 = new ChatChannel();
        chat1.setId(1L);
        ChatChannel chat2 = new ChatChannel();
        chat2.setId(2L);

        List<ChatChannel> mockChannels = Arrays.asList(chat1, chat2);

        when(chatChannelRepository.findByDoctorUserUsernameOrDoctorUserEmail(credential, credential))
                .thenReturn(mockChannels);

        List<ChatChannel> result = chatChannelService.findChatChannelsByDoctorUsernameOrEmail(credential);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(chatChannelRepository, times(1))
                .findByDoctorUserUsernameOrDoctorUserEmail(credential, credential);
    }

}
