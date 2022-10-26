package com.beyt.anouncy.location.controller;

import com.beyt.anouncy.location.dto.LocationDTO;
import com.beyt.anouncy.location.entity.Location;
import com.beyt.anouncy.location.service.provider.LocationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationProviderManager locationProviderManager;

    @GetMapping("/point")
    private ResponseEntity<LocationDTO> findPointLocations(@RequestParam Double lat, @RequestParam Double lon) {
        List<Location> coordinate = locationProviderManager.findCoordinate(lat, lon);
        return ResponseEntity.ok(new LocationDTO(coordinate));
    }
}
