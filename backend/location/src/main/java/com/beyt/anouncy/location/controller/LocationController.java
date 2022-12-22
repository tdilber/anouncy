package com.beyt.anouncy.location.controller;

import com.beyt.anouncy.location.dto.LocationDTO;
import com.beyt.anouncy.location.entity.Location;
import com.beyt.anouncy.location.service.LocationService;
import com.beyt.anouncy.location.service.provider.LocationProviderManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(description = "/location/v1", name = "Location Service")
@RestController
@RequestMapping("/location/v1")
@RequiredArgsConstructor
public class LocationController {
    private final LocationProviderManager locationProviderManager;
    private final LocationService locationService;

    @GetMapping("/point")
    private ResponseEntity<LocationDTO> findPointLocations(@RequestParam Double lat, @RequestParam Double lon) {
        List<Location> coordinate = locationProviderManager.findCoordinate(lat, lon);
        return ResponseEntity.ok(new LocationDTO(coordinate));
    }

    @GetMapping("/{parentLocationId}")
    private ResponseEntity<LocationDTO> findLocations(@PathVariable Long parentLocationId) {
        LocationDTO dto = locationService.findAllByParentId(parentLocationId);
        return ResponseEntity.ok(dto);
    }
}
