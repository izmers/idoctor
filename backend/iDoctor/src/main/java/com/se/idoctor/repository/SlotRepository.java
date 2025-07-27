package com.se.idoctor.repository;

import com.se.idoctor.entity.Slot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SlotRepository extends CrudRepository<Slot, Long> {

    List<Slot> getSlotsByDoctorId(Long doctorId);
    List<Slot> getSlotsByDoctor_User_Username(String username);
    List<Slot> getSlotsByDoctorIdAndAppointmentIsNull(Long doctorId);

    @Query("SELECT s FROM Slot s WHERE s.freeDay < :today OR (s.freeDay = :today AND s.freeTime <= :now)")
    List<Slot> findExpiredSlots(@Param("today") LocalDate today, @Param("now") LocalTime now);
}
