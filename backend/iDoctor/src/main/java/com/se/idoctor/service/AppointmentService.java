package com.se.idoctor.service;

import com.se.idoctor.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    Appointment bookAppointment(Appointment appointment, Long doctorId, Long slotId);
    List<Appointment> getFutureAppointmentsOfUser();
    List<Appointment> getFutureAppointmentsOfDoctor();
}
