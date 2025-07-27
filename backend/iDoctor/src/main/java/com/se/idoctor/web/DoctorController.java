package com.se.idoctor.web;

import com.se.idoctor.dto.DoctorDto;
import com.se.idoctor.entity.Doctor;
import com.se.idoctor.service.DoctorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {
    private DoctorService doctorService;

    @GetMapping("/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(this.doctorService.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Doctor>> getDoctorsNearby() {
        return new ResponseEntity<>(this.doctorService.getDoctorsNearby(), HttpStatus.OK);
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<List<Doctor>> getDoctorsByName(@PathVariable String name) {
        return new ResponseEntity<>(this.doctorService.getDoctorsByName(name), HttpStatus.OK);
    }

    @GetMapping("/by-city/{city}")
    public ResponseEntity<List<Doctor>> getDoctorsByCity(@PathVariable String city) {
        return new ResponseEntity<>(this.doctorService.getDoctorsByCity(city), HttpStatus.OK);
    }

    @GetMapping("/by-country/{country}")
    public ResponseEntity<List<Doctor>> getDoctorsByCountry(@PathVariable String country) {
        return new ResponseEntity<>(this.doctorService.getDoctorsByCountry(country), HttpStatus.OK);
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<Doctor> getDoctorByUsername(@PathVariable String username) {
        return new ResponseEntity<>(this.doctorService.getDoctorByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/by-type/{type}")
    public ResponseEntity<List<Doctor>> getDoctorsByType(@PathVariable String type) {
        return new ResponseEntity<>(this.doctorService.getDoctorsByType(type), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Doctor>> filterDoctors(@RequestParam String country, @RequestParam String city, @RequestParam String type) {
        return new ResponseEntity<>(this.doctorService.filterDoctors(country, city, type), HttpStatus.OK);
    }

    @GetMapping("/current")
    public ResponseEntity<Doctor> getCurrentDoctor() {
        return new ResponseEntity<>(this.doctorService.getCurrentDoctor(), HttpStatus.OK);
    }

    @PostMapping("/recommended")
    public ResponseEntity<Set<Doctor>> getRecommendedDoctorsNearby(@RequestBody List<String> doctorTypes) {
        return new ResponseEntity<>(this.doctorService.getRecommendedDoctors(doctorTypes), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Doctor> registerDoctor(@Valid @RequestBody DoctorDto doctorDto) {
        return new ResponseEntity<>(this.doctorService.register(doctorDto), HttpStatus.CREATED);
    }

    @PutMapping("/approval/{username}")
    public ResponseEntity<Doctor> approveDoctorAccount(@PathVariable String username) {
        return new ResponseEntity<>(this.doctorService.approveDoctorAccount(username), HttpStatus.OK);
    }
}
