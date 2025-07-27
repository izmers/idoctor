package com.se.idoctor;

import com.se.idoctor.entity.Doctor;
import com.se.idoctor.entity.DoctorStatus;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.repository.DoctorRepository;
import com.se.idoctor.service.DoctorServiceImpl;
import com.se.idoctor.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTests {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    void testGetAllDoctors_Success() {
        Userx user1 = new Userx();
        user1.setUsername("doctor.jane");
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setUser(user1);

        Userx user2 = new Userx();
        user2.setUsername("doctor.john");
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setUser(user2);

        List<Doctor> mockDoctors = Arrays.asList(doctor1, doctor2);

        when(doctorRepository.findAll()).thenReturn(mockDoctors);

        List<Doctor> result = doctorService.getAllDoctors();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("doctor.jane", result.get(0).getUser().getUsername());
        assertEquals("doctor.john", result.get(1).getUser().getUsername());

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void testGetDoctorById_Success() {
        Long doctorId = 1L;
        Userx user = new Userx();
        user.setUsername("doctor.jane");

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(doctorId);
        mockDoctor.setUser(user);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(mockDoctor));

        Doctor result = doctorService.getDoctorById(doctorId);

        assertNotNull(result);
        assertEquals(doctorId, result.getId());
        assertEquals("doctor.jane", result.getUser().getUsername());

        verify(doctorRepository, times(1)).findById(doctorId);
    }

    @Test
    void testGetDoctorByUsername_Success() {
        String username = "doctor.jane";

        Userx mockUser = new Userx();
        mockUser.setUsername(username);

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        mockUser.setDoctor(mockDoctor);

        when(userService.getUserByUsername(username)).thenReturn(mockUser);

        Doctor result = doctorService.getDoctorByUsername(username);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(mockDoctor, result);

        verify(userService, times(1)).getUserByUsername(username);
    }

    @Test
    void testApproveDoctorAccount_Success() {
        String email = "doctor.jane@example.com";

        Userx mockUser = new Userx();
        mockUser.setEmail(email);

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        mockDoctor.setStatus(DoctorStatus.LOCKED);
        mockUser.setDoctor(mockDoctor);

        when(userService.getUserByEmail(email)).thenReturn(mockUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);

        Doctor result = doctorService.approveDoctorAccount(email);

        assertNotNull(result);
        assertEquals(DoctorStatus.UNLOCKED, result.getStatus(), "Doctor status should be UNLOCKED");

        verify(userService, times(1)).getUserByEmail(email);
        verify(doctorRepository, times(1)).save(mockDoctor);
    }

    @Test
    void testApproveDoctorAccount_UserNotFound() {
        String email = "unknown@example.com";
        when(userService.getUserByEmail(email)).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
                doctorService.approveDoctorAccount(email)
        );

        verify(userService, times(1)).getUserByEmail(email);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testApproveDoctorAccount_UserHasNoDoctor() {
        String email = "patient@example.com";
        Userx mockUser = new Userx();
        mockUser.setEmail(email);
        mockUser.setDoctor(null);

        when(userService.getUserByEmail(email)).thenReturn(mockUser);

        assertThrows(NullPointerException.class, () ->
                doctorService.approveDoctorAccount(email)
        );

        verify(userService, times(1)).getUserByEmail(email);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testGetDoctorsNearby_Success() {
        Userx mockUser = new Userx();
        mockUser.setCity("Vienna");
        mockUser.setCountry("Austria");

        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setPracticeName("Vienna Medical Center");

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setPracticeName("Austria Family Health");

        List<Doctor> mockDoctors = Arrays.asList(doctor1, doctor2);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(doctorRepository.getDoctorsByUserCountryAndUserCity("Austria", "Vienna")).thenReturn(mockDoctors);

        List<Doctor> result = doctorService.getDoctorsNearby();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Vienna Medical Center", result.get(0).getPracticeName());
        assertEquals("Austria Family Health", result.get(1).getPracticeName());

        verify(userService, times(1)).getCurrentUser();
        verify(doctorRepository, times(1)).getDoctorsByUserCountryAndUserCity("Austria", "Vienna");
    }

    @Test
    void testGetDoctorsNearby_NoDoctorsFound() {
        Userx mockUser = new Userx();
        mockUser.setCity("Salzburg");
        mockUser.setCountry("Austria");

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(doctorRepository.getDoctorsByUserCountryAndUserCity("Austria", "Salzburg")).thenReturn(List.of());

        List<Doctor> result = doctorService.getDoctorsNearby();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService, times(1)).getCurrentUser();
        verify(doctorRepository, times(1)).getDoctorsByUserCountryAndUserCity("Austria", "Salzburg");
    }


    @Test
    void testGetDoctorsByName_Success() {
        String searchName = "Jane";

        Userx user1 = new Userx();
        user1.setFullName("Dr. Jane Doe");
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setUser(user1);

        Userx user2 = new Userx();
        user2.setFullName("Dr. Janet Smith");
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setUser(user2);

        List<Doctor> mockDoctors = Arrays.asList(doctor1, doctor2);

        when(doctorRepository.getDoctorsByUserFullNameContainingIgnoreCase(searchName)).thenReturn(mockDoctors);

        List<Doctor> result = doctorService.getDoctorsByName(searchName);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dr. Jane Doe", result.get(0).getUser().getFullName());
        assertEquals("Dr. Janet Smith", result.get(1).getUser().getFullName());

        verify(doctorRepository, times(1)).getDoctorsByUserFullNameContainingIgnoreCase(searchName);
    }

    @Test
    void testGetDoctorsByName_NoDoctorsFound() {
        String searchName = "Nonexistent";
        when(doctorRepository.getDoctorsByUserFullNameContainingIgnoreCase(searchName)).thenReturn(List.of());

        List<Doctor> result = doctorService.getDoctorsByName(searchName);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorRepository, times(1)).getDoctorsByUserFullNameContainingIgnoreCase(searchName);
    }

    @Test
    void testGetDoctorsByCity_Success() {
        String city = "Vienna";

        Userx user1 = new Userx();
        user1.setFullName("Dr. Jane Doe");
        user1.setCity(city);
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setUser(user1);

        Userx user2 = new Userx();
        user2.setFullName("Dr. John Smith");
        user2.setCity(city);
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setUser(user2);

        List<Doctor> mockDoctors = Arrays.asList(doctor1, doctor2);

        when(doctorRepository.getDoctorsByUserCity(city)).thenReturn(mockDoctors);

        List<Doctor> result = doctorService.getDoctorsByCity(city);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dr. Jane Doe", result.get(0).getUser().getFullName());
        assertEquals("Dr. John Smith", result.get(1).getUser().getFullName());

        verify(doctorRepository, times(1)).getDoctorsByUserCity(city);
    }

    @Test
    void testGetDoctorsByCity_NoDoctorsFound() {
        String city = "Salzburg";
        when(doctorRepository.getDoctorsByUserCity(city)).thenReturn(List.of());

        List<Doctor> result = doctorService.getDoctorsByCity(city);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorRepository, times(1)).getDoctorsByUserCity(city);
    }

    @Test
    void testGetDoctorsByCountry_Success() {
        String country = "Austria";

        Userx user1 = new Userx();
        user1.setFullName("Dr. Jane Doe");
        user1.setCountry(country);
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setUser(user1);

        Userx user2 = new Userx();
        user2.setFullName("Dr. John Smith");
        user2.setCountry(country);
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setUser(user2);

        List<Doctor> mockDoctors = Arrays.asList(doctor1, doctor2);

        when(doctorRepository.getDoctorsByUserCountry(country)).thenReturn(mockDoctors);

        List<Doctor> result = doctorService.getDoctorsByCountry(country);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dr. Jane Doe", result.get(0).getUser().getFullName());
        assertEquals("Dr. John Smith", result.get(1).getUser().getFullName());

        verify(doctorRepository, times(1)).getDoctorsByUserCountry(country);
    }

    @Test
    void testGetDoctorsByCountry_NoDoctorsFound() {
        String country = "Germany";
        when(doctorRepository.getDoctorsByUserCountry(country)).thenReturn(List.of());

        List<Doctor> result = doctorService.getDoctorsByCountry(country);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorRepository, times(1)).getDoctorsByUserCountry(country);
    }

    @Test
    void testGetDoctorsByType_Success() {
        String doctorType = "Cardiologist";

        List<Doctor> mockDoctors = getDoctors(doctorType);

        when(doctorRepository.getDoctorsByDoctorType(doctorType)).thenReturn(mockDoctors);

        List<Doctor> result = doctorService.getDoctorsByType(doctorType);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dr. Jane Doe", result.get(0).getUser().getFullName());
        assertEquals("Dr. John Smith", result.get(1).getUser().getFullName());

        verify(doctorRepository, times(1)).getDoctorsByDoctorType(doctorType);
    }

    private static @NotNull List<Doctor> getDoctors(String doctorType) {
        Userx user1 = new Userx();
        user1.setFullName("Dr. Jane Doe");
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setUser(user1);
        doctor1.setDoctorType(doctorType);

        Userx user2 = new Userx();
        user2.setFullName("Dr. John Smith");
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setUser(user2);
        doctor2.setDoctorType(doctorType);

        return Arrays.asList(doctor1, doctor2);
    }

    @Test
    void testGetDoctorsByType_NoDoctorsFound() {
        String doctorType = "Neurologist";
        when(doctorRepository.getDoctorsByDoctorType(doctorType)).thenReturn(List.of());

        List<Doctor> result = doctorService.getDoctorsByType(doctorType);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorRepository, times(1)).getDoctorsByDoctorType(doctorType);
    }

    @Test
    void testFilterDoctors_Success() {
        String country = "Austria";
        String city = "Vienna";
        String type = "Cardiologist";

        List<Doctor> mockDoctors = getDoctors(country, city, type);

        when(doctorRepository.findDoctorsByFilter(country, city, type)).thenReturn(mockDoctors);

        List<Doctor> result = doctorService.filterDoctors(country, city, type);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dr. Jane Doe", result.get(0).getUser().getFullName());
        assertEquals("Dr. John Smith", result.get(1).getUser().getFullName());

        verify(doctorRepository, times(1)).findDoctorsByFilter(country, city, type);
    }

    private static @NotNull List<Doctor> getDoctors(String country, String city, String type) {
        Userx user1 = new Userx();
        user1.setFullName("Dr. Jane Doe");
        user1.setCountry(country);
        user1.setCity(city);
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setUser(user1);
        doctor1.setDoctorType(type);

        Userx user2 = new Userx();
        user2.setFullName("Dr. John Smith");
        user2.setCountry(country);
        user2.setCity(city);
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setUser(user2);
        doctor2.setDoctorType(type);

        return Arrays.asList(doctor1, doctor2);
    }

    @Test
    void testFilterDoctors_NoDoctorsFound() {
        String country = "Germany";
        String city = "Berlin";
        String type = "Neurologist";

        when(doctorRepository.findDoctorsByFilter(country, city, type)).thenReturn(List.of());

        List<Doctor> result = doctorService.filterDoctors(country, city, type);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorRepository, times(1)).findDoctorsByFilter(country, city, type);
    }

    @Test
    void testFilterDoctors_NullOrEmptyFilters() {
        String country = "";
        String city = null;
        String type = "Dentist";

        when(doctorRepository.findDoctorsByFilter(null, null, type)).thenReturn(List.of());

        List<Doctor> result = doctorService.filterDoctors(country, city, type);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorRepository, times(1)).findDoctorsByFilter(null, null, type);
    }

    @Test
    void testGetCurrentDoctor_Success() {
        Userx mockUser = new Userx();
        mockUser.setUsername("doctor.jane");

        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        mockDoctor.setUser(mockUser);

        mockUser.setDoctor(mockDoctor);

        when(userService.getCurrentUser()).thenReturn(mockUser);

        Doctor result = doctorService.getCurrentDoctor();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("doctor.jane", result.getUser().getUsername());

        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void testGetRecommendedDoctors_Success() {
        List<String> doctorTypes = Arrays.asList("Cardiologist", "Neurologist");

        Userx mockUser = new Userx();
        mockUser.setCity("Vienna");
        mockUser.setCountry("Austria");

        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDoctorType("Cardiologist");

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setDoctorType("Neurologist");

        Doctor doctor3 = new Doctor();
        doctor3.setId(3L);
        doctor3.setDoctorType("Cardiologist");

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(doctorRepository.findDoctorsByFilter("Austria", "Vienna", "Cardiologist"))
                .thenReturn(Arrays.asList(doctor1, doctor3));
        when(doctorRepository.findDoctorsByFilter("Austria", "Vienna", "Neurologist"))
                .thenReturn(List.of(doctor2));

        Set<Doctor> result = doctorService.getRecommendedDoctors(doctorTypes);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(doctor1));
        assertTrue(result.contains(doctor2));
        assertTrue(result.contains(doctor3));

        verify(userService, times(1)).getCurrentUser();
        verify(doctorRepository, times(1)).findDoctorsByFilter("Austria", "Vienna", "Cardiologist");
        verify(doctorRepository, times(1)).findDoctorsByFilter("Austria", "Vienna", "Neurologist");
    }

    @Test
    void testGetRecommendedDoctors_NoDoctorsFound() {
        List<String> doctorTypes = Arrays.asList("Orthopedic", "Dermatologist");

        Userx mockUser = new Userx();
        mockUser.setCity("Salzburg");
        mockUser.setCountry("Austria");

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(doctorRepository.findDoctorsByFilter("Austria", "Salzburg", "Orthopedic")).thenReturn(List.of());
        when(doctorRepository.findDoctorsByFilter("Austria", "Salzburg", "Dermatologist")).thenReturn(List.of());

        Set<Doctor> result = doctorService.getRecommendedDoctors(doctorTypes);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService, times(1)).getCurrentUser();
        verify(doctorRepository, times(1)).findDoctorsByFilter("Austria", "Salzburg", "Orthopedic");
        verify(doctorRepository, times(1)).findDoctorsByFilter("Austria", "Salzburg", "Dermatologist");
    }

    @Test
    void testGetRecommendedDoctors_NullOrEmptyDoctorTypes() {
        Userx mockUser = new Userx();
        mockUser.setCity("Linz");
        mockUser.setCountry("Austria");

        when(userService.getCurrentUser()).thenReturn(mockUser);

        Set<Doctor> result = doctorService.getRecommendedDoctors(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(userService, times(1)).getCurrentUser();
        verify(doctorRepository, never()).findDoctorsByFilter(anyString(), anyString(), anyString());
    }

}
