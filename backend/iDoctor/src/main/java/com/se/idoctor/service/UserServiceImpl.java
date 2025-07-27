package com.se.idoctor.service;

import com.se.idoctor.dto.ResetPasswordDto;
import com.se.idoctor.dto.UserDto;
import com.se.idoctor.entity.PasswordResetToken;
import com.se.idoctor.entity.UserRole;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.exception.ConfirmPasswordException;
import com.se.idoctor.exception.EntityNotFoundException;
import com.se.idoctor.exception.PasswordResetTokenException;
import com.se.idoctor.repository.PasswordResetTokenRepository;
import com.se.idoctor.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private static final String PASSWORD_RESET_EXCEPTION_TEXT = "The password reset token of user with the username ";

    @Override
    public List<Userx> getAllUsers() {
        return (List<Userx>) this.userRepository.findAll();
    }

    @Override
    public Userx getUserById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Userx.class, id));
    }

    @Override
    public Userx getUserByEmail(String email) {
        return this.userRepository.findUserxByEmail(email).orElseThrow(() -> new EntityNotFoundException(Userx.class, email));
    }

    @Override
    public Userx getUserByUsername(String username) {
        return this.userRepository.findUserxByUsername(username).orElseThrow(() -> new EntityNotFoundException(Userx.class, username));
    }

    @Override
    public Userx getUserByUsernameOrEmail(String cred1, String cred2) {
        return this.userRepository.findUserxByUsernameOrEmail(cred1, cred2).orElseThrow(() -> new EntityNotFoundException(Userx.class, cred1));
    }

    public Userx registerUser(UserDto userDto, Set<UserRole> roles) {
        Userx newUser = new Userx();
        newUser.setEmail(userDto.getEmail());
        newUser.setFullName(userDto.getFullName());
        newUser.setUsername(userDto.getUsername());
        newUser.setCountry(userDto.getCountry());
        newUser.setCity(userDto.getCity());
        newUser.setZip(userDto.getZip());

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new ConfirmPasswordException("The password and confirmation password of user do not match.");
        }
        newUser.setPassword(this.bCryptPasswordEncoder.encode(userDto.getPassword()));
        newUser.setRoles(roles);
        return newUser;
    }

    @Override
    public Userx register(UserDto userDto) {
        return this.userRepository.save(registerUser(userDto, Set.of(UserRole.USER)));
    }

    @Override
    public Userx getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return this.getUserByUsernameOrEmail(username, username);
    }

    @Override
    public void verifyResetPassword(Long id) {
        Userx user = this.getUserById(id);
        PasswordResetToken passwordResetToken = user.getPasswordResetToken();

        if (passwordResetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            passwordResetToken.setVerified(false);
            this.passwordResetTokenRepository.save(passwordResetToken);
            throw new PasswordResetTokenException(PASSWORD_RESET_EXCEPTION_TEXT + user.getUsername() + " has expired.");
        }

        passwordResetToken.setVerified(true);
        this.passwordResetTokenRepository.save(passwordResetToken);
        this.userRepository.save(user);
    }

    @Override
    public Userx resetPassword(String email, ResetPasswordDto resetPasswordDto) {
        Userx user = this.getUserByEmail(email);
        PasswordResetToken passwordResetToken = user.getPasswordResetToken();
        if (passwordResetToken == null) {
            throw new PasswordResetTokenException("User with username " + user.getUsername() + " has not password reset token.");
        }

        if (passwordResetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            passwordResetToken.setVerified(false);
            this.passwordResetTokenRepository.save(passwordResetToken);
            throw new PasswordResetTokenException(PASSWORD_RESET_EXCEPTION_TEXT + user.getUsername() + " has expired.");
        }

        if (!passwordResetToken.isVerified()) {
            throw new PasswordResetTokenException(PASSWORD_RESET_EXCEPTION_TEXT + user.getUsername() + " is not verified.");
        }

        if (!resetPasswordDto.password().equals(resetPasswordDto.confirmPassword())) {
            throw new ConfirmPasswordException("Password mismatch.");
        }

        user.setPassword(this.bCryptPasswordEncoder.encode(resetPasswordDto.password()));
        user.setPasswordResetToken(null);
        this.passwordResetTokenRepository.delete(passwordResetToken);
        return this.userRepository.save(user);
    }
}
