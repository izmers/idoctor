package com.se.idoctor.service;

import com.se.idoctor.dto.ResetPasswordDto;
import com.se.idoctor.dto.UserDto;
import com.se.idoctor.entity.UserRole;
import com.se.idoctor.entity.Userx;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<Userx> getAllUsers();
    Userx getUserById(Long id);
    Userx getUserByEmail(String email);
    Userx getUserByUsername(String username);
    Userx getUserByUsernameOrEmail(String cred1, String cred2);
    Userx registerUser(UserDto userDto, Set<UserRole> roles);
    Userx register(UserDto userDto);
    Userx getCurrentUser();
    void verifyResetPassword(Long id);
    Userx resetPassword(String email, ResetPasswordDto resetPasswordDto);
}
