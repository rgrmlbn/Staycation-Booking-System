package com.spring.backend.module.user.user.dto.request;

import com.spring.backend.module.user.user.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 5, max = 50)
    private String name;

    private Gender gender;

    @Past(message = "Provide a valid birthdate")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String contactNumber;

    @Size(min = 8, max = 80)
    private String address;

    @Email(message = "Provide a valid email address") // Validates email
    private String email; // New email
}
