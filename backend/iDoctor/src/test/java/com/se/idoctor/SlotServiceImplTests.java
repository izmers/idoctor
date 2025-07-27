package com.se.idoctor;

import com.se.idoctor.entity.Doctor;
import com.se.idoctor.entity.DoctorStatus;
import com.se.idoctor.entity.Slot;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.exception.EntityNotFoundException;
import com.se.idoctor.exception.LockedDoctorRequestException;
import com.se.idoctor.exception.UserIsNotDoctorException;
import com.se.idoctor.repository.SlotRepository;
import com.se.idoctor.service.DoctorService;
import com.se.idoctor.service.SlotServiceImpl;
import com.se.idoctor.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
class SlotServiceImplTests {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private DoctorService doctorService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SlotServiceImpl slotService;

    @BeforeEach
    void setUp() {
        slotService = Mockito.spy(slotService);
    }


    @Test
    void testGetSlotById_Success() {
        Long slotId = 1L;
        Slot mockSlot = new Slot();
        mockSlot.setId(slotId);

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(mockSlot));

        Slot result = slotService.getSlotById(slotId);

        assertNotNull(result);
        assertEquals(slotId, result.getId());

        verify(slotRepository, times(1)).findById(slotId);
    }

    @Test
    void testGetSlotById_NotFound() {
        Long slotId = 99L;
        when(slotRepository.findById(slotId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                slotService.getSlotById(slotId)
        );

        verify(slotRepository, times(1)).findById(slotId);
    }

    @Test
    void testGetSlotByDoctorId_Success() {
        Long doctorId = 1L;

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(doctorId);

        Slot slot1 = new Slot();
        slot1.setId(101L);
        slot1.setDoctor(mockDoctor);

        Slot slot2 = new Slot();
        slot2.setId(102L);
        slot2.setDoctor(mockDoctor);

        List<Slot> mockSlots = Arrays.asList(slot1, slot2);

        when(doctorService.getDoctorById(doctorId)).thenReturn(mockDoctor);
        when(slotRepository.getSlotsByDoctorId(doctorId)).thenReturn(mockSlots);

        List<Slot> result = slotService.getSlotByDoctorId(doctorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getId());
        assertEquals(102L, result.get(1).getId());

        verify(doctorService, times(1)).getDoctorById(doctorId);
        verify(slotRepository, times(1)).getSlotsByDoctorId(doctorId);
    }

    @Test
    void testGetSlotByDoctorId_NoSlotsFound() {
        Long doctorId = 2L;
        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(doctorId);

        when(doctorService.getDoctorById(doctorId)).thenReturn(mockDoctor);
        when(slotRepository.getSlotsByDoctorId(doctorId)).thenReturn(List.of());

        List<Slot> result = slotService.getSlotByDoctorId(doctorId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorService, times(1)).getDoctorById(doctorId);
        verify(slotRepository, times(1)).getSlotsByDoctorId(doctorId);
    }

    @Test
    void testGetSlotByDoctorUsername_Success() {
        String username = "doctor.jane";

        Userx mockUser = new Userx();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setUserIsDoctor(true);

        Slot slot1 = new Slot();
        slot1.setId(101L);

        Slot slot2 = new Slot();
        slot2.setId(102L);

        List<Slot> mockSlots = Arrays.asList(slot1, slot2);

        when(userService.getUserByUsernameOrEmail(username, username)).thenReturn(mockUser);
        when(slotRepository.getSlotsByDoctor_User_Username(username)).thenReturn(mockSlots);

        List<Slot> result = slotService.getSlotByDoctorUsername(username);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getId());
        assertEquals(102L, result.get(1).getId());

        verify(userService, times(1)).getUserByUsernameOrEmail(username, username);
        verify(slotRepository, times(1)).getSlotsByDoctor_User_Username(username);
    }

    @Test
    void testGetSlotByDoctorUsername_UserIsNotDoctor() {
        String username = "patient.john";

        Userx mockUser = new Userx();
        mockUser.setId(2L);
        mockUser.setUsername(username);
        mockUser.setUserIsDoctor(false);

        when(userService.getUserByUsernameOrEmail(username, username)).thenReturn(mockUser);

        assertThrows(UserIsNotDoctorException.class, () ->
                slotService.getSlotByDoctorUsername(username)
        );

        verify(userService, times(1)).getUserByUsernameOrEmail(username, username);
        verify(slotRepository, never()).getSlotsByDoctor_User_Username(anyString());
    }

    @Test
    void testGetSlotByDoctorUsername_NoSlotsFound() {
        String username = "doctor.smith";

        Userx mockUser = new Userx();
        mockUser.setId(3L);
        mockUser.setUsername(username);
        mockUser.setUserIsDoctor(true);

        when(userService.getUserByUsernameOrEmail(username, username)).thenReturn(mockUser);
        when(slotRepository.getSlotsByDoctor_User_Username(username)).thenReturn(List.of());

        List<Slot> result = slotService.getSlotByDoctorUsername(username);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService, times(1)).getUserByUsernameOrEmail(username, username);
        verify(slotRepository, times(1)).getSlotsByDoctor_User_Username(username);
    }

    @Test
    void testGetAvailableSlotsOfDoctor_Success() {
        Long doctorId = 1L;

        Slot slot1 = new Slot();
        slot1.setId(101L);

        Slot slot2 = new Slot();
        slot2.setId(102L);

        List<Slot> mockAvailableSlots = Arrays.asList(slot1, slot2);

        when(slotRepository.getSlotsByDoctorIdAndAppointmentIsNull(doctorId)).thenReturn(mockAvailableSlots);

        List<Slot> result = slotService.getAvailableSlotsOfDoctor(doctorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getId());
        assertEquals(102L, result.get(1).getId());

        verify(slotRepository, times(1)).getSlotsByDoctorIdAndAppointmentIsNull(doctorId);
    }

    @Test
    void testGetAvailableSlotsOfDoctor_NoSlotsFound() {
        Long doctorId = 2L;
        when(slotRepository.getSlotsByDoctorIdAndAppointmentIsNull(doctorId)).thenReturn(List.of());

        List<Slot> result = slotService.getAvailableSlotsOfDoctor(doctorId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(slotRepository, times(1)).getSlotsByDoctorIdAndAppointmentIsNull(doctorId);
    }

    @Test
    void testCreateSlot_Success() {
        Userx mockDoctorUser = new Userx();
        mockDoctorUser.setId(1L);
        mockDoctorUser.setUserIsDoctor(true);

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(101L);
        mockDoctor.setStatus(DoctorStatus.UNLOCKED);
        mockDoctorUser.setDoctor(mockDoctor);

        Slot mockSlot = new Slot();
        mockSlot.setId(201L);

        when(userService.getCurrentUser()).thenReturn(mockDoctorUser);
        when(slotRepository.save(mockSlot)).thenReturn(mockSlot);

        Slot result = slotService.createSlot(mockSlot);

        assertNotNull(result);
        assertEquals(201L, result.getId());
        assertEquals(mockDoctor, result.getDoctor());

        verify(userService, times(1)).getCurrentUser();
        verify(slotRepository, times(1)).save(mockSlot);
    }

    @Test
    void testCreateSlot_UserIsNotDoctor() {
        Userx mockUser = new Userx();
        mockUser.setId(2L);
        mockUser.setUserIsDoctor(false);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        Slot slot = new Slot();

        assertThrows(UserIsNotDoctorException.class, () ->
                slotService.createSlot(slot)
        );

        verify(userService, times(1)).getCurrentUser();
        verify(slotRepository, never()).save(any());
    }

    @Test
    void testCreateSlot_DoctorIsLocked() {
        Userx mockDoctorUser = new Userx();
        mockDoctorUser.setId(3L);
        mockDoctorUser.setUserIsDoctor(true);

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(102L);
        mockDoctor.setStatus(DoctorStatus.LOCKED);
        mockDoctorUser.setDoctor(mockDoctor);

        when(userService.getCurrentUser()).thenReturn(mockDoctorUser);

        Slot slot = new Slot();

        assertThrows(LockedDoctorRequestException.class, () ->
                slotService.createSlot(slot)
        );

        verify(userService, times(1)).getCurrentUser();
        verify(slotRepository, never()).save(any());
    }

    @Test
    void testCreateSlots_Success() {
        Userx mockDoctorUser = new Userx();
        mockDoctorUser.setId(1L);
        mockDoctorUser.setUserIsDoctor(true);

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(101L);
        mockDoctor.setStatus(DoctorStatus.UNLOCKED);
        mockDoctorUser.setDoctor(mockDoctor);

        Slot slot1 = new Slot();
        slot1.setId(201L);

        Slot slot2 = new Slot();
        slot2.setId(202L);

        List<Slot> mockSlots = Arrays.asList(slot1, slot2);

        when(userService.getCurrentUser()).thenReturn(mockDoctorUser);
        when(slotRepository.saveAll(mockSlots)).thenReturn(mockSlots);

        List<Slot> result = slotService.createSlots(mockSlots);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(201L, result.get(0).getId());
        assertEquals(202L, result.get(1).getId());
        assertEquals(mockDoctor, result.get(0).getDoctor());
        assertEquals(mockDoctor, result.get(1).getDoctor());

        verify(userService, times(1)).getCurrentUser();
        verify(slotRepository, times(1)).saveAll(mockSlots);
    }

    @Test
    void testCreateSlots_UserIsNotDoctor() {
        Userx mockUser = new Userx();
        mockUser.setId(2L);
        mockUser.setUserIsDoctor(false);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        List<Slot> slots = Arrays.asList(new Slot(), new Slot());

        assertThrows(UserIsNotDoctorException.class, () ->
                slotService.createSlots(slots)
        );

        verify(userService, times(1)).getCurrentUser();
        verify(slotRepository, never()).saveAll(any());
    }

    @Test
    void testCreateSlots_DoctorIsLocked() {
        Userx mockDoctorUser = new Userx();
        mockDoctorUser.setId(3L);
        mockDoctorUser.setUserIsDoctor(true);

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(102L);
        mockDoctor.setStatus(DoctorStatus.LOCKED);
        mockDoctorUser.setDoctor(mockDoctor);

        when(userService.getCurrentUser()).thenReturn(mockDoctorUser);

        List<Slot> slots = Arrays.asList(new Slot(), new Slot());

        assertThrows(LockedDoctorRequestException.class, () ->
                slotService.createSlots(slots)
        );

        verify(userService, times(1)).getCurrentUser();
        verify(slotRepository, never()).saveAll(any());
    }

    @Test
    void testGetAvailableSlotsByDate_Success() {
        Long doctorId = 1L;
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalDate futureDate = today.plusDays(2);

        Slot pastSlot = new Slot();
        pastSlot.setId(101L);
        pastSlot.setFreeDay(today);
        pastSlot.setFreeTime(now.minusHours(1));

        Slot validSlotToday = new Slot();
        validSlotToday.setId(102L);
        validSlotToday.setFreeDay(today);
        validSlotToday.setFreeTime(now.plusHours(1));

        Slot validSlotFuture = new Slot();
        validSlotFuture.setId(103L);
        validSlotFuture.setFreeDay(futureDate);
        validSlotFuture.setFreeTime(LocalTime.NOON);

        List<Slot> availableSlots = Arrays.asList(pastSlot, validSlotToday, validSlotFuture);

        when(slotService.getAvailableSlotsOfDoctor(doctorId)).thenReturn(availableSlots);

        List<Slot> result = slotService.getAvailableSlotsByDate(doctorId, today);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(102L, result.getFirst().getId());

        verify(slotService, times(1)).getAvailableSlotsOfDoctor(doctorId);
    }

    @Test
    void testGetAvailableSlotsByDate_FutureDate() {
        Long doctorId = 1L;
        LocalDate futureDate = LocalDate.now().plusDays(3);

        Slot futureSlot = new Slot();
        futureSlot.setId(104L);
        futureSlot.setFreeDay(futureDate);
        futureSlot.setFreeTime(LocalTime.NOON);

        List<Slot> availableSlots = List.of(futureSlot);

        when(slotService.getAvailableSlotsOfDoctor(doctorId)).thenReturn(availableSlots);

        List<Slot> result = slotService.getAvailableSlotsByDate(doctorId, futureDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(104L, result.getFirst().getId());

        verify(slotService, times(1)).getAvailableSlotsOfDoctor(doctorId);
    }

    @Test
    void testGetAvailableSlotsByDate_NoMatchingSlots() {
        Long doctorId = 2L;
        LocalDate today = LocalDate.now();

        when(slotService.getAvailableSlotsOfDoctor(doctorId)).thenReturn(List.of());

        List<Slot> result = slotService.getAvailableSlotsByDate(doctorId, today);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(slotService, times(1)).getAvailableSlotsOfDoctor(doctorId);
    }

    @Test
    void testDeleteSlotById_Success() {
        Long slotId = 1L;
        Slot mockSlot = new Slot();
        mockSlot.setId(slotId);

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(mockSlot));

        slotService.deleteSlotById(slotId);

        verify(slotRepository, times(1)).findById(slotId);
        verify(slotRepository, times(1)).deleteById(slotId);
    }

    @Test
    void testDeleteSlotById_SlotNotFound() {
        Long slotId = 99L;
        when(slotRepository.findById(slotId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                slotService.deleteSlotById(slotId)
        );

        verify(slotRepository, times(1)).findById(slotId);
        verify(slotRepository, never()).deleteById(anyLong());
    }
}
