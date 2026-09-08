package com.spring.backend.module.property.controller;

import com.spring.backend.module.property.dto.request.AmenityCreateRequest;
import com.spring.backend.module.property.dto.request.AmenityUpdateRequest;
import com.spring.backend.module.property.dto.response.AmenityResponse;
import com.spring.backend.module.property.service.impl.AmenityServiceImpl;
import com.spring.backend.module.property.service.interfaces.AmenityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amenities")
@RequiredArgsConstructor
@Validated
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping
    ResponseEntity<List<AmenityResponse>> getAllAmenities() {
        return ResponseEntity.ok().body(amenityService.getAllAmenities());
    }

    @GetMapping("/{id}")
    ResponseEntity<AmenityResponse> getAmenityById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok().body(amenityService.getAmenityById(id));
    }

    @PostMapping({"/create"})
    ResponseEntity<AmenityResponse> createAmenity(@RequestBody @Valid AmenityCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(amenityService.createAmenity(request));
    }

    @PatchMapping("/update/{id}")
    ResponseEntity<AmenityResponse> updateAmenity(@PathVariable @Positive Long id, @RequestBody @Valid AmenityUpdateRequest update) {
        return ResponseEntity.ok().body(amenityService.updateAmenity(id, update));
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteAmenity(@PathVariable @Positive Long id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }
}

