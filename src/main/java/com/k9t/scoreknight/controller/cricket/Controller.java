package com.k9t.scoreknight.controller.cricket;

import com.k9t.scoreknight.dto.cricket.CricketMatchCardDto;
import com.k9t.scoreknight.dto.cricket.HomeMatchesResponseDto;
import com.k9t.scoreknight.service.cricket.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cricket")
@RequiredArgsConstructor
public class Controller {

    private final Service cricketService;

    @GetMapping("/home")
    public ResponseEntity<HomeMatchesResponseDto> getHomeMatches() {
        return ResponseEntity.ok(cricketService.getHomeMatches());
    }

    @GetMapping("/matches/{matchId}")
    public ResponseEntity<CricketMatchCardDto> getMatchDetails(@PathVariable String matchId) {
        return ResponseEntity.ok(cricketService.getMatchDetails(matchId));
    }
}

