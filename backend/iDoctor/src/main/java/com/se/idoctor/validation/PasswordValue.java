package com.se.idoctor.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValueValidator.class)
public @interface PasswordValue {

    String message() default "Password is too weak or invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
