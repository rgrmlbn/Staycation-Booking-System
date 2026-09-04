package com.spring.backend.module.user.user.dto.request;

import com.spring.backend.module.user.user.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(min = 2, max = 20)
    private String firstName;

    @Size(min = 1, max = 20)
    private String middleName;

    @Size(min = 2, max = 20)
    private String lastName;

    @Size(min = 1, max = 8)
    private String suffix;

    private Gender gender;

    @Past(message = "Provide a valid birthdate")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String contactNumber;

    private String street;

    private String barangay;

    private String city;

    private String province;

    private String postalCode;

    @Email(message = "Provide a valid email address") // Validates email
    private String email; // New email
}
