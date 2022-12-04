package com.beyt.anouncy.announce.controller;

import com.beyt.anouncy.announce.dto.AnnounceCreateDTO;
import com.beyt.anouncy.announce.service.AnnounceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/announce")
@RequiredArgsConstructor
public class AnnounceController {
    private final AnnounceService announceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> receiveAnnounce(@RequestBody @Valid AnnounceCreateDTO dto) {
        announceService.receiveAnnounce(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }


}
