package com.se.idoctor.repository;

import com.se.idoctor.entity.Doctor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository extends CrudRepository<Doctor, Long> {

    List<Doctor> getDoctorsByUserCity(String city);
    List<Doctor> getDoctorsByUserCountry(String country);
    List<Doctor> getDoctorsByUserFullNameContainingIgnoreCase(String name);
    List<Doctor> getDoctorsByDoctorType(String type);
    List<Doctor> getDoctorsByUserCountryAndUserCity(String country, String city);

    @Query("SELECT d FROM Doctor d " +
            "JOIN d.user u " +
            "WHERE (:country IS NULL OR LOWER(u.country) = LOWER(:country)) " +
            "AND (:city IS NULL OR LOWER(u.city) = LOWER(:city)) " +
            "AND (:doctorType IS NULL OR LOWER(d.doctorType) = LOWER(:doctorType))")
    List<Doctor> findDoctorsByFilter(
            @Param("country") String country,
            @Param("city") String city,
            @Param("doctorType") String doctorType);
}
