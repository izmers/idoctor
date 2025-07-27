package com.se.idoctor.repository;

import com.se.idoctor.entity.Appointment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends CrudRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND (a.slot.freeDay > :today OR (a.slot.freeDay = :today AND a.slot.freeTime > :now))")
    List<Appointment> findFutureAppointmentsByUserId(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now
    );

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND (a.slot.freeDay > :today OR (a.slot.freeDay = :today AND a.slot.freeTime > :now))")
    List<Appointment> findFutureAppointmentsByDoctorId(
            @Param("doctorId") Long doctorId,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now
    );
}
