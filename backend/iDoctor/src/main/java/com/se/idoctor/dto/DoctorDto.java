package com.se.idoctor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    @Valid
    @NotNull
    private UserDto userDto;
    private String practiceName;

    @NotNull
    private String doctorType;

    @NotNull
    private String phoneNumber;
}
