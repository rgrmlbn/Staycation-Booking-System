package com.spring.backend.module.user.user.dto.response;

import com.spring.backend.module.user.user.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
    private int age;
    private String contactNumber;
    private String address;
    private String email;
    private String role;

}
