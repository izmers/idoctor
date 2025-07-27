package com.se.idoctor.web;

import com.se.idoctor.entity.Appointment;
import com.se.idoctor.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
@AllArgsConstructor
public class AppointmentController {
    private AppointmentService appointmentService;

    @GetMapping("/future")
    public ResponseEntity<List<Appointment>> getAppointmentsOfUserInTheFuture() {
        return new ResponseEntity<>(this.appointmentService.getFutureAppointmentsOfUser(), HttpStatus.OK);
    }

    @GetMapping("/future/doctor")
    public ResponseEntity<List<Appointment>> getAppointmentsOfDoctorInTheFuture() {
        return new ResponseEntity<>(this.appointmentService.getFutureAppointmentsOfDoctor(), HttpStatus.OK);
    }

    @PostMapping("/book/{doctorId}/{slotId}")
    public ResponseEntity<Appointment> bookAppointment(@Valid @RequestBody Appointment appointment, @PathVariable Long doctorId, @PathVariable Long slotId) {
        return new ResponseEntity<>(this.appointmentService.bookAppointment(appointment, doctorId, slotId), HttpStatus.CREATED);
    }
}
