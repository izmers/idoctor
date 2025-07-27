package com.se.idoctor.service;

import com.se.idoctor.dto.DoctorDto;
import com.se.idoctor.entity.Doctor;
import com.se.idoctor.entity.DoctorStatus;
import com.se.idoctor.entity.UserRole;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.exception.EntityNotFoundException;
import com.se.idoctor.repository.DoctorRepository;
import com.se.idoctor.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DoctorServiceImpl implements DoctorService {
    private DoctorRepository doctorRepository;
    private UserService userService;
    private UserRepository userRepository;

    @Override
    public List<Doctor> getAllDoctors() {
        return (List<Doctor>) this.doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return this.doctorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Doctor.class, id));
    }

    @Override
    public Doctor getDoctorByUsername(String username) {
        Userx user = this.userService.getUserByUsername(username);
        return user.getDoctor();
    }

    @Override
    public Doctor register(DoctorDto doctorDto) {
        Userx user = this.userService.registerUser(doctorDto.getUserDto(), Set.of(UserRole.USER, UserRole.DOCTOR));
        user.setUserIsDoctor(true);
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setPracticeName(doctorDto.getPracticeName());
        doctor.setStatus(DoctorStatus.LOCKED);
        doctor.setDoctorType(doctorDto.getDoctorType());

        this.userRepository.save(user);
        return this.doctorRepository.save(doctor);
    }

    @Override
    public Doctor approveDoctorAccount(String email) {
        Userx user = this.userService.getUserByEmail(email);
        Doctor doctor = user.getDoctor();
        doctor.setStatus(DoctorStatus.UNLOCKED);
        return this.doctorRepository.save(doctor);
    }

    @Override
    public List<Doctor> getDoctorsNearby() {
        Userx user = this.userService.getCurrentUser();
        String userCity = user.getCity();
        String userCountry = user.getCountry();
        return this.doctorRepository.getDoctorsByUserCountryAndUserCity(userCountry, userCity);
    }

    @Override
    public List<Doctor> getDoctorsByName(String name) {
        return this.doctorRepository.getDoctorsByUserFullNameContainingIgnoreCase(name);
    }

    @Override
    public List<Doctor> getDoctorsByCity(String city) {
        return this.doctorRepository.getDoctorsByUserCity(city);
    }

    @Override
    public List<Doctor> getDoctorsByCountry(String country) {
        return this.doctorRepository.getDoctorsByUserCountry(country);
    }

    @Override
    public List<Doctor> getDoctorsByType(String type) {
        return this.doctorRepository.getDoctorsByDoctorType(type);
    }

    @Override
    public List<Doctor> filterDoctors(String country, String city, String type) {
        country = (country != null && !country.isEmpty()) ? country : null;
        city = (city != null && !city.isEmpty()) ? city : null;
        type = (type != null && !type.isEmpty()) ? type : null;
        return this.doctorRepository.findDoctorsByFilter(country, city, type);
    }

    @Override
    public Doctor getCurrentDoctor() {
        Userx user = this.userService.getCurrentUser();
        return user.getDoctor();
    }

    @Override
    public Set<Doctor> getRecommendedDoctors(List<String> doctorTypes) {
        System.out.println("the doctor types: " + doctorTypes);
        Userx user = this.userService.getCurrentUser();
        String userCity = user.getCity();
        String userCountry = user.getCountry();
        return doctorTypes.stream().flatMap(type -> this.doctorRepository.findDoctorsByFilter(userCountry, userCity, type).stream()).collect(Collectors.toSet());
    }
}
