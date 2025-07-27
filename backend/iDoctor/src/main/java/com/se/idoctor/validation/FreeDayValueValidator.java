package com.se.idoctor.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class FreeDayValueValidator implements ConstraintValidator<FreeDayValue, LocalDate> {

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate today = LocalDate.now();
        return localDate.isEqual(today) || localDate.isAfter(today);
    }
}
