package com.se.idoctor;

import com.se.idoctor.dto.UserDto;
import com.se.idoctor.entity.UserRole;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.exception.ConfirmPasswordException;
import com.se.idoctor.exception.EntityNotFoundException;
import com.se.idoctor.repository.UserRepository;
import com.se.idoctor.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetAllUsers_Success() {
        Userx user1 = new Userx();
        user1.setId(1L);
        user1.setUsername("user1");

        Userx user2 = new Userx();
        user2.setId(2L);
        user2.setUsername("user2");

        List<Userx> mockUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<Userx> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());

        verify(userRepository, times(1)).findAll();
    }


    @Test
    void testGetAllUsers_NoUsersFound() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<Userx> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_Success() {
        Long userId = 1L;
        Userx mockUser = new Userx();
        mockUser.setId(userId);
        mockUser.setUsername("testUser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Userx result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_UserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getUserById(userId)
        );

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByEmail_Success() {
        String email = "test@example.com";
        Userx mockUser = new Userx();
        mockUser.setId(1L);
        mockUser.setEmail(email);
        mockUser.setUsername("testUser");

        when(userRepository.findUserxByEmail(email)).thenReturn(Optional.of(mockUser));

        Userx result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("testUser", result.getUsername());

        verify(userRepository, times(1)).findUserxByEmail(email);
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findUserxByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getUserByEmail(email)
        );

        verify(userRepository, times(1)).findUserxByEmail(email);
    }

    @Test
    void testGetUserByUsername_Success() {
        String username = "testUser";
        Userx mockUser = new Userx();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setEmail("test@example.com");

        when(userRepository.findUserxByUsername(username)).thenReturn(Optional.of(mockUser));

        Userx result = userService.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository, times(1)).findUserxByUsername(username);
    }


    @Test
    void testGetUserByUsername_UserNotFound() {
        String username = "nonexistentUser";
        when(userRepository.findUserxByUsername(username)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getUserByUsername(username)
        );

        verify(userRepository, times(1)).findUserxByUsername(username);
    }


    @Test
    void testGetUserByUsernameOrEmail_Success() {
        String username = "testUser";
        String email = "test@example.com";
        Userx mockUser = new Userx();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setEmail(email);

        when(userRepository.findUserxByUsernameOrEmail(username, email)).thenReturn(Optional.of(mockUser));

        Userx result = userService.getUserByUsernameOrEmail(username, email);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());

        verify(userRepository, times(1)).findUserxByUsernameOrEmail(username, email);
    }

    @Test
    void testGetUserByUsernameOrEmail_UserNotFound() {
        String username = "nonexistentUser";
        String email = "nonexistent@example.com";
        when(userRepository.findUserxByUsernameOrEmail(username, email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getUserByUsernameOrEmail(username, email)
        );

        verify(userRepository, times(1)).findUserxByUsernameOrEmail(username, email);
    }

    @Test
    void testRegisterUser_Success() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFullName("Test User");
        userDto.setUsername("testuser");
        userDto.setCountry("Austria");
        userDto.setCity("Vienna");
        userDto.setZip(1010L);
        userDto.setPassword("SecurePass123");
        userDto.setConfirmPassword("SecurePass123");

        Set<UserRole> roles = Set.of(UserRole.USER);

        when(bCryptPasswordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword123");

        Userx registeredUser = userService.registerUser(userDto, roles);

        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("Test User", registeredUser.getFullName());
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("Austria", registeredUser.getCountry());
        assertEquals("Vienna", registeredUser.getCity());
        assertEquals(1010L, registeredUser.getZip());
        assertEquals("encodedPassword123", registeredUser.getPassword());
        assertEquals(roles, registeredUser.getRoles());

        verify(bCryptPasswordEncoder, times(1)).encode(userDto.getPassword());
    }

    @Test
    void testRegisterUser_PasswordMismatch() {
        UserDto userDto = new UserDto();
        userDto.setPassword("SecurePass123");
        userDto.setConfirmPassword("DifferentPass123");

        Set<UserRole> roles = Set.of(UserRole.USER);

        Exception exception = assertThrows(ConfirmPasswordException.class, () ->
                userService.registerUser(userDto, roles)
        );

        assertEquals("The password and confirmation password of user do not match.", exception.getMessage());

        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

}
