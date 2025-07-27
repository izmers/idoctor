package com.se.idoctor;

import com.se.idoctor.dto.ChatRequestDto;
import com.se.idoctor.entity.*;
import com.se.idoctor.exception.ChatRequestAlreadyExistsException;
import com.se.idoctor.repository.ChatChannelRepository;
import com.se.idoctor.repository.ChatRequestRepository;
import com.se.idoctor.service.ChatRequestServiceImpl;
import com.se.idoctor.service.DoctorService;
import com.se.idoctor.service.UserService;
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
class ChatRequestServiceImplTests {

    @Mock
    private ChatRequestRepository chatRequestRepository;

    @Mock
    private ChatChannelRepository chatChannelRepository;

    @Mock
    private UserService userService;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private ChatRequestServiceImpl chatRequestService;


    @Test
    void testGetChatRequestById_Success() {
        Long requestId = 1L;
        ChatRequest mockRequest = new ChatRequest();
        mockRequest.setId(requestId);

        when(chatRequestRepository.findById(requestId)).thenReturn(Optional.of(mockRequest));

        ChatRequest result = chatRequestService.getChatRequestById(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());

        verify(chatRequestRepository, times(1)).findById(requestId);
    }


    @Test
    void testGetChatRequestsByDoctorUsername_Success() {
        String doctorUsername = "dr.jane";
        ChatRequest chatRequest1 = new ChatRequest();
        chatRequest1.setId(1L);
        chatRequest1.setChatRequestStatus(ChatRequestStatus.PENDING);

        ChatRequest chatRequest2 = new ChatRequest();
        chatRequest2.setId(2L);
        chatRequest2.setChatRequestStatus(ChatRequestStatus.PENDING);

        List<ChatRequest> mockRequests = Arrays.asList(chatRequest1, chatRequest2);

        when(chatRequestRepository.findByDoctorUsernameAndChatRequestStatus(doctorUsername, ChatRequestStatus.PENDING))
                .thenReturn(mockRequests);

        List<ChatRequest> result = chatRequestService.getChatRequestsByDoctorUsername(doctorUsername);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ChatRequestStatus.PENDING, result.get(0).getChatRequestStatus());
        assertEquals(ChatRequestStatus.PENDING, result.get(1).getChatRequestStatus());

        verify(chatRequestRepository, times(1))
                .findByDoctorUsernameAndChatRequestStatus(doctorUsername, ChatRequestStatus.PENDING);
    }

    @Test
    void testCreateChatRequestWithDto_NewRequest_Success() {
        // Arrange
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setDoctorUsername("doctor.jane");
        chatRequestDto.setUserUsername("john.doe");
        chatRequestDto.setUserNote("Need consultation");

        Userx mockUser = new Userx();
        mockUser.setUsername("john.doe");

        Doctor mockDoctor = new Doctor();

        mockUser.setDoctor(mockDoctor);

        ChatRequest mockChatRequest = new ChatRequest();
        mockChatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);
        mockChatRequest.setUser(mockUser);
        mockChatRequest.setDoctor(mockDoctor);
        mockChatRequest.setUserNote("Need consultation");

        when(chatRequestRepository.findChatRequestByDoctorUserUsernameAndUserUsername("doctor.jane", "john.doe"))
                .thenReturn(Optional.empty());

        when(userService.getUserByUsernameOrEmail("john.doe", "john.doe"))
                .thenReturn(mockUser);

        when(userService.getUserByUsernameOrEmail("doctor.jane", "doctor.jane"))
                .thenReturn(mockUser); // Because Doctor is part of Userx

        when(chatRequestRepository.save(any(ChatRequest.class)))
                .thenReturn(mockChatRequest);

        ChatRequest result = chatRequestService.createChatRequestWithDto(chatRequestDto);

        assertNotNull(result);
        assertEquals(ChatRequestStatus.PENDING, result.getChatRequestStatus());
        assertEquals("Need consultation", result.getUserNote());
        assertEquals(mockUser, result.getUser());
        assertEquals(mockDoctor, result.getDoctor());

        verify(chatRequestRepository, times(1)).findChatRequestByDoctorUserUsernameAndUserUsername("doctor.jane", "john.doe");
        verify(userService, times(1)).getUserByUsernameOrEmail("john.doe", "john.doe");
        verify(userService, times(1)).getUserByUsernameOrEmail("doctor.jane", "doctor.jane");
        verify(chatRequestRepository, times(1)).save(any(ChatRequest.class));
    }

    @Test
    void testCreateChatRequestWithDto_ExistingRequest_InactiveChannel_Success() {
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setDoctorUsername("doctor.jane");
        chatRequestDto.setUserUsername("john.doe");
        chatRequestDto.setUserNote("Need consultation again");

        ChatRequest existingChatRequest = new ChatRequest();
        existingChatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);
        existingChatRequest.setUserNote("Old request");

        ChatChannel inactiveChatChannel = new ChatChannel();
        inactiveChatChannel.setChatChannelStatus(ChatChannelStatus.INACTIVE);

        when(chatRequestRepository.findChatRequestByDoctorUserUsernameAndUserUsername("doctor.jane", "john.doe"))
                .thenReturn(Optional.of(existingChatRequest));

        when(chatChannelRepository.findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe"))
                .thenReturn(Optional.of(inactiveChatChannel));

        when(chatRequestRepository.save(any(ChatRequest.class)))
                .thenReturn(existingChatRequest);

        ChatRequest result = chatRequestService.createChatRequestWithDto(chatRequestDto);

        assertNotNull(result);
        assertEquals(ChatRequestStatus.PENDING, result.getChatRequestStatus());
        assertEquals("Need consultation again", result.getUserNote());

        verify(chatRequestRepository, times(1)).findChatRequestByDoctorUserUsernameAndUserUsername("doctor.jane", "john.doe");
        verify(chatChannelRepository, times(1)).findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe");
        verify(chatRequestRepository, times(1)).save(any(ChatRequest.class));
    }

    @Test
    void testCreateChatRequestWithDto_ExistingRequest_ActiveChannel_ExceptionThrown() {
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setDoctorUsername("doctor.jane");
        chatRequestDto.setUserUsername("john.doe");

        ChatRequest existingChatRequest = new ChatRequest();
        existingChatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);

        ChatChannel activeChatChannel = new ChatChannel();
        activeChatChannel.setChatChannelStatus(ChatChannelStatus.ACTIVE);

        when(chatRequestRepository.findChatRequestByDoctorUserUsernameAndUserUsername("doctor.jane", "john.doe"))
                .thenReturn(Optional.of(existingChatRequest));

        when(chatChannelRepository.findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe"))
                .thenReturn(Optional.of(activeChatChannel));

        assertThrows(ChatRequestAlreadyExistsException.class, () ->
                chatRequestService.createChatRequestWithDto(chatRequestDto)
        );

        verify(chatRequestRepository, times(1)).findChatRequestByDoctorUserUsernameAndUserUsername("doctor.jane", "john.doe");
        verify(chatChannelRepository, times(1)).findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe");

        verify(chatRequestRepository, times(0)).save(any(ChatRequest.class));
    }

    @Test
    void testCreateChatRequest_Success() {
        Long doctorId = 1L;
        ChatRequest chatRequest = new ChatRequest();

        Userx mockUser = new Userx();
        mockUser.setId(100L);
        mockUser.setUsername("john.doe");

        Userx mockUserDoctor = new Userx();
        mockUser.setId(100L);
        Doctor mockDoctor = new Doctor();
        mockDoctor.setUser(mockUserDoctor);
        mockDoctor.setId(doctorId);
        mockDoctor.getUser().setUsername("doctor.jane");

        ChatRequest savedChatRequest = new ChatRequest();
        savedChatRequest.setId(200L);
        savedChatRequest.setUser(mockUser);
        savedChatRequest.setDoctor(mockDoctor);
        savedChatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(doctorService.getDoctorById(doctorId)).thenReturn(mockDoctor);
        when(chatRequestRepository.save(any(ChatRequest.class))).thenReturn(savedChatRequest);

        ChatRequest result = chatRequestService.createChatRequest(chatRequest, doctorId);

        assertNotNull(result);
        assertEquals(ChatRequestStatus.PENDING, result.getChatRequestStatus());
        assertEquals(mockUser, result.getUser());
        assertEquals(mockDoctor, result.getDoctor());

        verify(userService, times(1)).getCurrentUser();
        verify(doctorService, times(1)).getDoctorById(doctorId);
        verify(chatRequestRepository, times(1)).save(any(ChatRequest.class));
    }

    @Test
    void testAcceptChatRequest_ExistingChatChannel_Success() {
        Long chatRequestId = 1L;
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setDoctorUsername("doctor.jane");
        chatRequestDto.setUserUsername("john.doe");
        chatRequestDto.setDoctorNote("Approved for consultation");

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setId(chatRequestId);
        chatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);

        ChatChannel existingChatChannel = new ChatChannel();
        existingChatChannel.setChatChannelStatus(ChatChannelStatus.INACTIVE);

        when(chatRequestRepository.findById(chatRequestId)).thenReturn(Optional.of(chatRequest));
        when(chatChannelRepository.findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe"))
                .thenReturn(Optional.of(existingChatChannel));
        when(chatChannelRepository.save(any(ChatChannel.class))).thenReturn(existingChatChannel);
        when(chatRequestRepository.save(any(ChatRequest.class))).thenReturn(chatRequest);

        ChatRequest result = chatRequestService.acceptChatRequest(chatRequestDto, chatRequestId);

        assertNotNull(result);
        assertEquals(ChatRequestStatus.ACCEPTED, result.getChatRequestStatus());
        assertEquals("Approved for consultation", result.getDoctorNote());
        assertEquals(ChatChannelStatus.ACTIVE, existingChatChannel.getChatChannelStatus());

        verify(chatRequestRepository, times(1)).findById(chatRequestId);
        verify(chatChannelRepository, times(1))
                .findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe");
        verify(chatChannelRepository, times(1)).save(existingChatChannel);
        verify(chatRequestRepository, times(1)).save(chatRequest);
    }

    @Test
    void testAcceptChatRequest_NewChatChannel_Success() {
        Long chatRequestId = 2L;
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setDoctorUsername("doctor.jane");
        chatRequestDto.setUserUsername("john.doe");
        chatRequestDto.setDoctorNote("Consultation approved");

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setId(chatRequestId);
        chatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);

        when(chatRequestRepository.findById(chatRequestId)).thenReturn(Optional.of(chatRequest));
        when(chatChannelRepository.findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe"))
                .thenReturn(Optional.empty());

        when(chatChannelRepository.save(any(ChatChannel.class)))
                .thenAnswer(invocation -> {
                    ChatChannel savedChatChannel = invocation.getArgument(0);
                    assertEquals(ChatChannelStatus.ACTIVE, savedChatChannel.getChatChannelStatus());
                    assertEquals(chatRequest.getDoctor(), savedChatChannel.getDoctor());
                    assertEquals(chatRequest.getUser(), savedChatChannel.getPatient());
                    return savedChatChannel;
                });

        when(chatRequestRepository.save(any(ChatRequest.class))).thenReturn(chatRequest);

        ChatRequest result = chatRequestService.acceptChatRequest(chatRequestDto, chatRequestId);

        assertNotNull(result);
        assertEquals(ChatRequestStatus.ACCEPTED, result.getChatRequestStatus());
        assertEquals("Consultation approved", result.getDoctorNote());

        verify(chatRequestRepository, times(1)).findById(chatRequestId);
        verify(chatChannelRepository, times(1))
                .findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe");
        verify(chatChannelRepository, times(1)).save(any(ChatChannel.class));
        verify(chatRequestRepository, times(1)).save(chatRequest);
    }

    @Test
    void testAcceptChatRequest_ChatRequestNotFound_ExceptionThrown() {
        Long chatRequestId = 3L;
        ChatRequestDto chatRequestDto = new ChatRequestDto();

        when(chatRequestRepository.findById(chatRequestId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                chatRequestService.acceptChatRequest(chatRequestDto, chatRequestId)
        );

        verify(chatRequestRepository, times(1)).findById(chatRequestId);
        verify(chatChannelRepository, times(0)).findChatChannelByDoctorUserUsernameAndPatientUsername(anyString(), anyString());
        verify(chatChannelRepository, times(0)).save(any(ChatChannel.class));
        verify(chatRequestRepository, times(0)).save(any(ChatRequest.class));
    }

    @Test
    void testDeclineChatRequest_ExistingChatChannel_Success() {
        Long chatRequestId = 1L;
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setDoctorUsername("doctor.jane");
        chatRequestDto.setUserUsername("john.doe");
        chatRequestDto.setDoctorNote("Not available at the moment");

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setId(chatRequestId);
        chatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);

        ChatChannel existingChatChannel = new ChatChannel();
        existingChatChannel.setChatChannelStatus(ChatChannelStatus.ACTIVE);

        when(chatRequestRepository.findById(chatRequestId)).thenReturn(Optional.of(chatRequest));
        when(chatChannelRepository.findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe"))
                .thenReturn(Optional.of(existingChatChannel));
        when(chatChannelRepository.save(any(ChatChannel.class))).thenReturn(existingChatChannel);
        when(chatRequestRepository.save(any(ChatRequest.class))).thenReturn(chatRequest);

        ChatRequest result = chatRequestService.declineChatRequest(chatRequestDto, chatRequestId);

        assertNotNull(result);
        assertEquals(ChatRequestStatus.DECLINED, result.getChatRequestStatus());
        assertEquals("Not available at the moment", result.getDoctorNote());
        assertEquals(ChatChannelStatus.INACTIVE, existingChatChannel.getChatChannelStatus());
        assertEquals("Not available at the moment", existingChatChannel.getDoctorNote());

        verify(chatRequestRepository, times(1)).findById(chatRequestId);
        verify(chatChannelRepository, times(1))
                .findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe");
        verify(chatChannelRepository, times(1)).save(existingChatChannel);
        verify(chatRequestRepository, times(1)).save(chatRequest);
    }

    @Test
    void testDeclineChatRequest_NewChatChannel_Success() {
        Long chatRequestId = 2L;
        ChatRequestDto chatRequestDto = new ChatRequestDto();
        chatRequestDto.setDoctorUsername("doctor.jane");
        chatRequestDto.setUserUsername("john.doe");
        chatRequestDto.setDoctorNote("Unavailable for consultation");

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setId(chatRequestId);
        chatRequest.setChatRequestStatus(ChatRequestStatus.PENDING);

        when(chatRequestRepository.findById(chatRequestId)).thenReturn(Optional.of(chatRequest));
        when(chatChannelRepository.findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe"))
                .thenReturn(Optional.empty());

        when(chatChannelRepository.save(any(ChatChannel.class)))
                .thenAnswer(invocation -> {
                    ChatChannel savedChatChannel = invocation.getArgument(0);
                    assertEquals(ChatChannelStatus.INACTIVE, savedChatChannel.getChatChannelStatus());
                    assertEquals(chatRequest.getDoctor(), savedChatChannel.getDoctor());
                    assertEquals(chatRequest.getUser(), savedChatChannel.getPatient());
                    assertEquals("Unavailable for consultation", savedChatChannel.getDoctorNote());
                    return savedChatChannel;
                });

        when(chatRequestRepository.save(any(ChatRequest.class))).thenReturn(chatRequest);

        ChatRequest result = chatRequestService.declineChatRequest(chatRequestDto, chatRequestId);

        assertNotNull(result);
        assertEquals(ChatRequestStatus.DECLINED, result.getChatRequestStatus());
        assertEquals("Unavailable for consultation", result.getDoctorNote());

        verify(chatRequestRepository, times(1)).findById(chatRequestId);
        verify(chatChannelRepository, times(1))
                .findChatChannelByDoctorUserUsernameAndPatientUsername("doctor.jane", "john.doe");
        verify(chatChannelRepository, times(1)).save(any(ChatChannel.class));
        verify(chatRequestRepository, times(1)).save(chatRequest);
    }

    @Test
    void testDeclineChatRequest_ChatRequestNotFound_ExceptionThrown() {
        Long chatRequestId = 3L;
        ChatRequestDto chatRequestDto = new ChatRequestDto();

        when(chatRequestRepository.findById(chatRequestId)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () ->
                chatRequestService.declineChatRequest(chatRequestDto, chatRequestId)
        );

        verify(chatRequestRepository, times(1)).findById(chatRequestId);
        verify(chatChannelRepository, times(0)).findChatChannelByDoctorUserUsernameAndPatientUsername(anyString(), anyString());
        verify(chatChannelRepository, times(0)).save(any(ChatChannel.class));
        verify(chatRequestRepository, times(0)).save(any(ChatRequest.class));
    }

}
