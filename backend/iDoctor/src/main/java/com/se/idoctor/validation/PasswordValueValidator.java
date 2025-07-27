package com.se.idoctor.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PasswordValueValidator implements ConstraintValidator<PasswordValue, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        char[] passwordCharArray = s.toCharArray();
        return s.length() >= 12 && atLeastOneUppercaseLetter(passwordCharArray) && atLeastOneLowercaseLetter(passwordCharArray) && atLeastOneDigit(passwordCharArray) && atLeastTwoSpecialCharacters(passwordCharArray);
    }

    private boolean atLeastOneUppercaseLetter(char[] password) {
        for (char c : password) {
            if (Character.isAlphabetic(c) && Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean atLeastOneLowercaseLetter(char[] password) {
        for (char c : password) {
            if (Character.isAlphabetic(c) && Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean atLeastOneDigit(char[] password) {
        for (char c : password) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean atLeastTwoSpecialCharacters(char[] password) {
        Set<Character> specialCharacters = new HashSet<>(Arrays.asList('@', '#', '!', '$', '%', '&', '*', '?', '+', '-', '=', '(', ')'));
        int count = 0;

        for (char c : password) {
            if (specialCharacters.contains(c)) {
                count++;
            }
        }
        return count >= 2;
    }
}
