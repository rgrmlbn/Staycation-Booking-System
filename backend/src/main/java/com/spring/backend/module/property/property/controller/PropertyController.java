package com.spring.backend.module.property.property.controller;

import com.spring.backend.module.property.property.dto.request.PropertyCreateRequest;
import com.spring.backend.module.property.property.dto.request.PropertyUpdateRequest;
import com.spring.backend.module.property.property.dto.response.PropertyDetailedResponse;
import com.spring.backend.module.property.property.dto.response.PropertySummaryResponse;
import com.spring.backend.module.property.property.enums.PropertyStatus;
import com.spring.backend.module.property.property.service.interfaces.PropertyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
@Validated
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping("/summary")
    ResponseEntity<Page<PropertySummaryResponse>> getAllSummaryProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam (defaultValue = "10")  int size,
            @RequestParam (required = false ) String name
    )
    {

        return ResponseEntity.ok().body(propertyService.getAllSummaryProperties(page, size, name));

    }

    @GetMapping("/detailed")
    ResponseEntity<Page<PropertyDetailedResponse>> getAllDetailedProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam (defaultValue = "10")  int size,
            @RequestParam (required = false ) String name
    )
    {

        return ResponseEntity.ok().body(propertyService.getAllDetailedProperties(page, size, name));

    }

    @GetMapping("/detailed/status")
    ResponseEntity<Page<PropertyDetailedResponse>> getAllDetailedPropertiesByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam (defaultValue = "10")  int size,
            @RequestParam (required = false ) PropertyStatus status
    )
    {

        return ResponseEntity.ok().body(propertyService.getAllDetailedPropertiesByStatus(page, size, status));

    }

    @GetMapping("/detailed/{id}")
    ResponseEntity<PropertyDetailedResponse> getPropertyById(@PathVariable @Positive Long id){
        return ResponseEntity.ok().body(propertyService.getPropertyById(id));

    }

    @PostMapping("/create")
    ResponseEntity<PropertyDetailedResponse> createProperty(@Valid @RequestBody PropertyCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.createProperty(request));
    }

    @PatchMapping("/update/{id}")
    ResponseEntity<PropertyDetailedResponse> updateProperty(@PathVariable @Positive Long id, @Valid @RequestBody PropertyUpdateRequest update){
        return ResponseEntity.ok().body(propertyService.updateProperty(id, update));
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteProperty(@PathVariable @Positive Long id){
        propertyService.deleteProperty(id);

        return ResponseEntity.noContent().build();
    }
}
