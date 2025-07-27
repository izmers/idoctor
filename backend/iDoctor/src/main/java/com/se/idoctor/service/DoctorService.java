package com.se.idoctor.service;

import com.se.idoctor.dto.DoctorDto;
import com.se.idoctor.entity.Doctor;

import java.util.List;
import java.util.Set;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(Long id);
    Doctor getDoctorByUsername(String username);
    Doctor register(DoctorDto doctorDto);
    Doctor approveDoctorAccount(String username);
    List<Doctor> getDoctorsNearby();
    List<Doctor> getDoctorsByName(String name);
    List<Doctor> getDoctorsByCity(String city);
    List<Doctor> getDoctorsByCountry(String country);
    List<Doctor> getDoctorsByType(String type);
    List<Doctor> filterDoctors(String country, String city, String type);
    Doctor getCurrentDoctor();
    Set<Doctor> getRecommendedDoctors(List<String> doctorTypes);
}
