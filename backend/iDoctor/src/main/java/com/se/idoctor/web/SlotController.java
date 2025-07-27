package com.se.idoctor.web;

import com.se.idoctor.entity.Slot;
import com.se.idoctor.service.SlotService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/slot")
public class SlotController {
    private SlotService slotService;

    @GetMapping("/{id}")
    public ResponseEntity<Slot> getSlotById(@PathVariable Long id) {
        return new ResponseEntity<>(this.slotService.getSlotById(id), HttpStatus.OK);
    }

    @GetMapping("/doctor-id/{doctorId}")
    public ResponseEntity<List<Slot>> getSlotByDoctorId(@PathVariable Long doctorId) {
        return new ResponseEntity<>(this.slotService.getSlotByDoctorId(doctorId), HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<Slot>> getSlotByDoctorUsername(@PathVariable String username) {
        return new ResponseEntity<>(this.slotService.getSlotByDoctorUsername(username), HttpStatus.OK);
    }

    @GetMapping("/available/{doctorId}")
    public ResponseEntity<List<Slot>> getAvailableSlotsOfDoctor(@PathVariable Long doctorId) {
        return new ResponseEntity<>(this.slotService.getAvailableSlotsOfDoctor(doctorId), HttpStatus.OK);
    }

    @GetMapping("/available/by-date/{doctorId}/{date}")
    public ResponseEntity<List<Slot>> getAvailableSlotsOfDoctorByDate(@PathVariable Long doctorId, @PathVariable LocalDate date) {
        return new ResponseEntity<>(this.slotService.getAvailableSlotsByDate(doctorId, date), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Slot> createSlot(@Valid @RequestBody Slot slot) {
        return new ResponseEntity<>(this.slotService.createSlot(slot), HttpStatus.CREATED);
    }

    @PostMapping("/bulk/create")
    public ResponseEntity<List<Slot>> createSlots(@Valid @RequestBody List<Slot> slots) {
        return new ResponseEntity<>(this.slotService.createSlots(slots), HttpStatus.CREATED);
    }
}
