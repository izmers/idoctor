package com.se.idoctor.service;

import com.se.idoctor.entity.Appointment;
import com.se.idoctor.entity.Doctor;
import com.se.idoctor.entity.Slot;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.exception.UserIsNotDoctorException;
import com.se.idoctor.repository.AppointmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentRepository appointmentRepository;
    private DoctorService doctorService;
    private SlotService slotService;
    private UserService userService;

    @Override
    public Appointment bookAppointment(Appointment appointment, Long doctorId, Long slotId) {
        Doctor doctor = this.doctorService.getDoctorById(doctorId);
        Slot slot = this.slotService.getSlotById(slotId);
        Userx user = this.userService.getCurrentUser();

        appointment.setDoctor(doctor);
        appointment.setSlot(slot);
        appointment.setUser(user);

        return this.appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getFutureAppointmentsOfUser() {
        return this.appointmentRepository.findFutureAppointmentsByUserId(this.userService.getCurrentUser().getId(), LocalDate.now(), LocalTime.now());
    }

    @Override
    public List<Appointment> getFutureAppointmentsOfDoctor() {
        Userx user = this.userService.getCurrentUser();
        if (!user.isUserIsDoctor()) {
            throw new UserIsNotDoctorException(user.getId());
        }
        return this.appointmentRepository.findFutureAppointmentsByDoctorId(user.getDoctor().getId(), LocalDate.now(), LocalTime.now());
    }
}
