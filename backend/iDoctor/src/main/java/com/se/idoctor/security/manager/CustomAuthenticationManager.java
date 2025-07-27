package com.se.idoctor.security.manager;

import com.se.idoctor.entity.DoctorStatus;
import com.se.idoctor.entity.Userx;
import com.se.idoctor.exception.LockedDoctorLoginException;
import com.se.idoctor.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Userx user = this.userService.getUserByUsernameOrEmail(authentication.getName(), authentication.getName());

        if (user.getDoctor() != null && user.getDoctor().getStatus() == DoctorStatus.LOCKED) {
            throw new LockedDoctorLoginException(user.getDoctor().getId());
        }

        if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("The provided password was incorrect");
        }
        return new UsernamePasswordAuthenticationToken(authentication.getName(), user.getPassword());
    }
}
