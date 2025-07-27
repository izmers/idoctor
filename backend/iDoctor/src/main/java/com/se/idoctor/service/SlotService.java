package com.se.idoctor.service;

import com.se.idoctor.entity.Slot;

import java.time.LocalDate;
import java.util.List;

public interface SlotService {
    Slot getSlotById(Long id);
    List<Slot> getSlotByDoctorId(Long doctorId);
    List<Slot> getSlotByDoctorUsername(String username);
    List<Slot> getAvailableSlotsOfDoctor(Long doctorId);
    Slot createSlot(Slot slot);
    List<Slot> createSlots(List<Slot> slots);
    List<Slot> getAvailableSlotsByDate(Long doctorId, LocalDate date);
    void deleteExpiredSlots();
    void deleteSlotById(Long id);
}
