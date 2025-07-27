package com.se.idoctor.dto;

import com.se.idoctor.validation.PasswordValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String fullName;

    @NotNull
    private String username;

    @NotNull
    private Long zip;

    @NotNull
    private String city;

    @NotNull
    private String country;

    @PasswordValue
    private String password;
    private String confirmPassword;
}
