package com.spring.backend.module.user.auth.dto.request;


import com.spring.backend.module.user.user.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    private  String name;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Provide a valid birthdate")
    private  LocalDate dateOfBirth;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^9\\d{9}$", message = "Invalid phone number format")
    private  String contactNumber;

    @NotBlank(message = "Address is required")
    @Size(min = 8, max = 80, message = "Address must be between 8 and 80 characters")
    private  String address;


    @NotBlank(message = "Email is required")
    @Email(message = "Provide a valid email")
    private  String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character"
    )
    private  String password;

}
