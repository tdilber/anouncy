package com.beyt.anouncy.listing.controller;

import com.beyt.anouncy.common.entity.redis.AnnouncePageDTO;
import com.beyt.anouncy.listing.dto.enumeration.AnnounceListingType;
import com.beyt.anouncy.listing.service.AnnounceListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/announce-list")
@RequiredArgsConstructor
public class AnnounceListingController {
    private final AnnounceListingService announceListingService;

    @GetMapping("/{regionId}/{type}")
    private ResponseEntity<AnnouncePageDTO> findLocations(@PathVariable String regionId, @PathVariable AnnounceListingType type, @RequestParam @Valid @Min(0) Integer page) throws Exception {
        AnnouncePageDTO dto = announceListingService.list(regionId, type, page);
        return ResponseEntity.ok(dto);
    }
}
