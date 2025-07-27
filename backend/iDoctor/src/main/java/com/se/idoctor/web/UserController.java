package com.se.idoctor.web;

import com.se.idoctor.dto.ResetPasswordDto;
import com.se.idoctor.dto.UserDto;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/userx")
public class UserController {
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<Userx>> getAllUsers() {
        return new ResponseEntity<>(this.userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/current")
    public ResponseEntity<Userx> getCurrentUser() {
        return new ResponseEntity<>(this.userService.getCurrentUser(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Userx> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(this.userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Userx> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(this.userService.getUserByEmail(email), HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Userx> getUserByUsername(@PathVariable String username) {
        return new ResponseEntity<>(this.userService.getUserByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/cred/{username}/{email}")
    public ResponseEntity<Userx> getUserByUsernameOrEmail(@PathVariable String username, @PathVariable String email) {
        return new ResponseEntity<>(this.userService.getUserByUsernameOrEmail(username, email), HttpStatus.OK);
    }

    @GetMapping("/verify-reset-password/{userId}")
    public ResponseEntity<String> verifyResetPassword(@PathVariable Long userId) {
        this.userService.verifyResetPassword(userId);
        return ResponseEntity.ok("Email verified.");
    }

    @PutMapping("/reset-password/{userEmail}")
    public ResponseEntity<Userx> resetPassword(@PathVariable String userEmail, @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        return new ResponseEntity<>(this.userService.resetPassword(userEmail, resetPasswordDto), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Userx> registerUser(@Valid @RequestBody UserDto user) {
        return new ResponseEntity<>(this.userService.register(user), HttpStatus.CREATED);
    }
}
