package com.spring.backend.module.user.user.controller;

import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.service.interfaces.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Valid
public class UserController {

    private final UserService userService;

    @GetMapping()
    ResponseEntity <List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getMe());
    }

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getUserById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/{id}")
    ResponseEntity<UserResponse> updateUserById(@PathVariable @Positive Long id, @RequestBody @Valid UpdateUserRequest update) {
        return ResponseEntity.ok(userService.updateUserById(id, update));
    }

    @PatchMapping("/{id}/change-password")
    ResponseEntity<Void> changePasswordById(@PathVariable @Positive Long id, @RequestBody @Valid ChangePasswordRequest changePassword) {
        userService.changePasswordById(id, changePassword);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUserById(@PathVariable @Positive Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
