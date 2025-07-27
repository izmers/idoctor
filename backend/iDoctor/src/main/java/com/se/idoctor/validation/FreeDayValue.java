package com.se.idoctor.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FreeDayValueValidator.class)
public @interface FreeDayValue {

    String message() default "The free day date must be today or in the future.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
