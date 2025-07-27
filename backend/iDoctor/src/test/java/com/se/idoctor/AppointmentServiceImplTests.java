package com.se.idoctor;

import com.se.idoctor.entity.Appointment;
import com.se.idoctor.entity.Doctor;
import com.se.idoctor.entity.Slot;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.exception.UserIsNotDoctorException;
import com.se.idoctor.repository.AppointmentRepository;
import com.se.idoctor.service.AppointmentServiceImpl;
import com.se.idoctor.service.DoctorService;
import com.se.idoctor.service.SlotService;
import com.se.idoctor.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTests {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorService doctorService;

    @Mock
    private SlotService slotService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void testBookAppointment() {
        Long doctorId = 1L;
        Long slotId = 1L;

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(doctorId);

        Slot mockSlot = new Slot();
        mockSlot.setId(slotId);

        Userx mockUser = new Userx();
        mockUser.setId(1L);

        Appointment mockAppointment = new Appointment();
        mockAppointment.setId(1L);

        when(doctorService.getDoctorById(doctorId)).thenReturn(mockDoctor);
        when(slotService.getSlotById(slotId)).thenReturn(mockSlot);
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

        Appointment result = appointmentService.bookAppointment(mockAppointment, doctorId, slotId);

        assertEquals(mockDoctor, result.getDoctor());
        assertEquals(mockSlot, result.getSlot());
        assertEquals(mockUser, result.getUser());

        verify(doctorService, times(1)).getDoctorById(doctorId);
        verify(slotService, times(1)).getSlotById(slotId);
        verify(userService, times(1)).getCurrentUser();
        verify(appointmentRepository, times(1)).save(mockAppointment);
    }

    @Test
    void testGetFutureAppointmentsOfUser() {
        Userx mockUser = new Userx();
        mockUser.setId(1L);
        when(userService.getCurrentUser()).thenReturn(mockUser);

        when(appointmentRepository.findFutureAppointmentsByUserId(eq(1L), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(new ArrayList<>(Arrays.asList(new Appointment(), new Appointment())));

        List<Appointment> result = appointmentService.getFutureAppointmentsOfUser();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(appointmentRepository, times(1))
                .findFutureAppointmentsByUserId(eq(1L), any(LocalDate.class), any(LocalTime.class));
    }

    @Test
    void testGetFutureAppointmentsOfDoctor() {
        Userx mockUser = new Userx();
        mockUser.setUserIsDoctor(true);
        mockUser.setId(1L);

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        mockUser.setDoctor(mockDoctor);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(appointmentRepository.findFutureAppointmentsByDoctorId(eq(1L), any(LocalDate.class), any(LocalTime.class))).thenReturn(new ArrayList<>(Arrays.asList(new Appointment(), new Appointment())));

        List<Appointment> result = appointmentService.getFutureAppointmentsOfDoctor();
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(appointmentRepository, times(1))
                .findFutureAppointmentsByDoctorId(eq(1L), any(LocalDate.class), any(LocalTime.class));
    }

    @Test
    void testGetFutureAppointmentsOfDoctor_UserIsNotDoctor() {
        Userx mockUser = new Userx();
        mockUser.setId(1L);

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        mockUser.setDoctor(mockDoctor);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        assertThrows(UserIsNotDoctorException.class, () -> appointmentService.getFutureAppointmentsOfDoctor());

        verify(appointmentRepository, times(0))
                .findFutureAppointmentsByDoctorId(eq(1L), any(LocalDate.class), any(LocalTime.class));
    }

}
