package com.spring.backend.module.user.auth.dto.response;

import com.spring.backend.module.user.user.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RegisterResponse {

    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    private Gender gender;

    private LocalDate dateOfBirth;

    private int age;

    private String contactNumber;

    private String street;

    private String barangay;

    private String city;

    private String province;

    private String postalCode;

    private String email;

    private String role;
}
