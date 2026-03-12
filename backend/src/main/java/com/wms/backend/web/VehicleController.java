package com.wms.backend.web;

import com.wms.backend.domain.enums.VehicleStatus;
import com.wms.backend.domain.model.Vehicle;
import com.wms.backend.repo.VehicleRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Validated
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @PreAuthorize("hasRole('FLEET_MANAGER') or hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Vehicle>> list() {
        return ResponseEntity.ok(vehicleRepository.findAll());
    }

    public record UpsertVehicleRequest(
            @NotBlank String licensePlate,
            String vehicleType,
            Integer capacity,
            @NotNull VehicleStatus status
    ) {
    }

    @PreAuthorize("hasRole('FLEET_MANAGER') or hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<Vehicle> create(@RequestBody UpsertVehicleRequest req) {
        Vehicle v = new Vehicle();
        v.setLicensePlate(req.licensePlate());
        v.setVehicleType(req.vehicleType());
        v.setCapacity(req.capacity());
        v.setStatus(req.status());
        return ResponseEntity.ok(vehicleRepository.save(v));
    }

    @PreAuthorize("hasRole('FLEET_MANAGER') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> update(@PathVariable Long id, @RequestBody UpsertVehicleRequest req) {
        Vehicle v = vehicleRepository.findById(id).orElseThrow();
        v.setLicensePlate(req.licensePlate());
        v.setVehicleType(req.vehicleType());
        v.setCapacity(req.capacity());
        v.setStatus(req.status());
        return ResponseEntity.ok(vehicleRepository.save(v));
    }
}

