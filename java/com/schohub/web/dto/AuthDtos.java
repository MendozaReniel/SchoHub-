package com.schohub.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record SignupRequest(
            @NotBlank(message = "Email is required.")
            @Email(message = "Enter a valid email address.")
            String email,

            @NotBlank(message = "Username is required.")
            @Size(min = 3, max = 30, message = "Username must be 3 to 30 characters.")
            String username,

            @NotBlank(message = "Password is required.")
            @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters.")
            String password) {
    }

    public record LoginRequest(
            @NotBlank(message = "Email is required.")
            @Email(message = "Enter a valid email address.")
            String email,

            @NotBlank(message = "Password is required.")
            String password) {
    }

    public record AuthResponse(String token, Instant expiresAt, StudentDtos.StudentResponse student) {
    }
}
