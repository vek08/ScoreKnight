package com.k9t.scoreknight.controller.cricket;

import com.k9t.scoreknight.dto.cricket.CricketMatchCardDto;
import com.k9t.scoreknight.service.cricket.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cricket")
@RequiredArgsConstructor
public class Controller {

    private final Service cricketService;

    @GetMapping("/home")
    public ResponseEntity<List<CricketMatchCardDto>> getHomeMatches() {

        return ResponseEntity.ok(cricketService.getHomeMatches());
    }
}