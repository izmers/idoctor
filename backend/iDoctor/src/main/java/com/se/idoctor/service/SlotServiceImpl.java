package com.se.idoctor.service;

import com.se.idoctor.entity.Doctor;
import com.se.idoctor.entity.DoctorStatus;
import com.se.idoctor.entity.Slot;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.exception.EntityNotFoundException;
import com.se.idoctor.exception.LockedDoctorRequestException;
import com.se.idoctor.exception.UserIsNotDoctorException;
import com.se.idoctor.repository.SlotRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class SlotServiceImpl implements SlotService {
    private SlotRepository slotRepository;
    private UserService userService;
    private DoctorService doctorService;

    @Override
    public Slot getSlotById(Long id) {
        return this.slotRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Slot.class, id));
    }

    @Override
    public List<Slot> getSlotByDoctorId(Long doctorId) {
        Doctor doctor = this.doctorService.getDoctorById(doctorId);
        return this.slotRepository.getSlotsByDoctorId(doctor.getId());
    }

    @Override
    public List<Slot> getSlotByDoctorUsername(String username) {
        Userx user = this.userService.getUserByUsernameOrEmail(username, username);
        if (!user.isUserIsDoctor()) {
            throw new UserIsNotDoctorException(user.getId());
        }
        return this.slotRepository.getSlotsByDoctor_User_Username(user.getUsername());
    }

    @Override
    public List<Slot> getAvailableSlotsOfDoctor(Long doctorId) {
        return this.slotRepository.getSlotsByDoctorIdAndAppointmentIsNull(doctorId);
    }

    @Override
    public Slot createSlot(Slot slot) {
        Userx user = this.userService.getCurrentUser();
        if (!user.isUserIsDoctor()) {
            throw new UserIsNotDoctorException(user.getId());
        }
        Doctor doctor = user.getDoctor();
        if (doctor.getStatus() == DoctorStatus.UNLOCKED) {
            slot.setDoctor(user.getDoctor());
        } else {
            throw new LockedDoctorRequestException(doctor.getId());
        }
        return this.slotRepository.save(slot);
    }

    @Override
    public List<Slot> createSlots(List<Slot> slots) {
        Userx user = this.userService.getCurrentUser();
        if (!user.isUserIsDoctor()) {
            throw new UserIsNotDoctorException(user.getId());
        }
        Doctor doctor = user.getDoctor();
        if (doctor.getStatus() == DoctorStatus.UNLOCKED) {
            slots.forEach(slot -> slot.setDoctor(doctor));
        } else {
            throw new LockedDoctorRequestException(doctor.getId());
        }
        return (List<Slot>) this.slotRepository.saveAll(slots);
    }

    @Override
    public List<Slot> getAvailableSlotsByDate(Long doctorId, LocalDate date) {
        List<Slot> availableSlots = this.getAvailableSlotsOfDoctor(doctorId).stream().filter(slot -> slot.getFreeDay().equals(date)).toList();
        List<Slot> futureAvailableSlots = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        for (Slot slot : availableSlots) {
            if (slot.getFreeDay().isAfter(today)) {
                futureAvailableSlots.add(slot);
            }

            if (slot.getFreeDay().isEqual(today) && slot.getFreeTime().isAfter(now)) {
                futureAvailableSlots.add(slot);
            }
        }
        return futureAvailableSlots;
    }

    public void deleteSlotById(Long id) {
        Slot slot = this.getSlotById(id);
        this.slotRepository.deleteById(slot.getId());
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredSlots() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Slot> expiredSlots = this.slotRepository.findExpiredSlots(today, now);

        for (Slot slot : expiredSlots) {
            this.deleteSlotById(slot.getId());
        }
    }
}
