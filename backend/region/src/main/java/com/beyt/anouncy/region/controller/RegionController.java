package com.beyt.anouncy.region.controller;

import com.beyt.anouncy.region.dto.RegionDTO;
import com.beyt.anouncy.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    @GetMapping("/{parentRegionId}")
    private ResponseEntity<RegionDTO> findLocations(@PathVariable String parentRegionId) {
        RegionDTO dto = regionService.findAllByParentId(parentRegionId);
        return ResponseEntity.ok(dto);
    }
}
