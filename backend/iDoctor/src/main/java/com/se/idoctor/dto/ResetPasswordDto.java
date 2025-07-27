package com.se.idoctor.dto;

import com.se.idoctor.validation.PasswordValue;

public record ResetPasswordDto(@PasswordValue String password, String confirmPassword) {
}
