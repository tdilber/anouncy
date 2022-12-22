package com.beyt.anouncy.announce.controller;

import com.beyt.anouncy.announce.dto.AnnounceCreateDTO;
import com.beyt.anouncy.announce.service.AnnounceService;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(description = "/announce/v1", name = "Announce Service (Select Announce Service)")
@RestController
@RequestMapping("/announce/v1")
@RequiredArgsConstructor
public class AnnounceController {
    private final AnnounceService announceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AnnouncePageItemDTO> receiveAnnounce(@RequestBody @Valid AnnounceCreateDTO dto) {
        AnnouncePageItemDTO returnDto = announceService.receiveAnnounce(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnDto);
    }

    @DeleteMapping("/{announceId}")
    public ResponseEntity<String> deleteAnnounce(@PathVariable String announceId) {
        announceService.deleteAnnounce(announceId);
        return ResponseEntity.ok("OK");
    }


    @GetMapping("/{announceId}")
    public ResponseEntity<AnnouncePageItemDTO> getAnnounce(@PathVariable String announceId) {
        AnnouncePageItemDTO dto = announceService.getAnnounce(announceId);
        return ResponseEntity.ok(dto);
    }


    @GetMapping
    public ResponseEntity<Page<AnnouncePageItemDTO>> getAnnounceList(Pageable pageable) {
        Page<AnnouncePageItemDTO> dto = announceService.getAnnounceList(pageable);
        return ResponseEntity.ok(dto);
    }
}
