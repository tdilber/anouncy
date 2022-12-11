package com.beyt.anouncy.vote.controller;


import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.vote.dto.VoteCreateDTO;
import com.beyt.anouncy.vote.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping("/{announceId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AnnouncePageItemDTO> receiveVote(@PathVariable String announceId, @RequestBody @Valid VoteCreateDTO dto) {
        AnnouncePageItemDTO returnDto = voteService.receiveVote(announceId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnDto);
    }

    @DeleteMapping("/{announceId}")
    public ResponseEntity<String> deleteVote(@PathVariable String announceId) {
        voteService.deleteVote(announceId);
        return ResponseEntity.ok("OK");
    }
}
