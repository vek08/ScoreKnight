package com.k9t.scoreknight.football.controller;

import com.k9t.scoreknight.football.dto.HomeFootballMatchesResponseDto;
import com.k9t.scoreknight.football.dto.FootballMatchCardDto;
import com.k9t.scoreknight.football.dto.FootballMatchDetailsDto;
import com.k9t.scoreknight.football.dto.FootballStandingDto;
import com.k9t.scoreknight.football.service.FootballService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/football")
public class FootballController {

    private final FootballService footballService;

    public FootballController(FootballService footballService) {
        this.footballService = footballService;
    }

    @GetMapping("/home")
    public HomeFootballMatchesResponseDto getHomeMatches() {
        return footballService.getHomeMatches();
    }

    @GetMapping("/matches")
    public List<FootballMatchCardDto> getMatchesByDate(@RequestParam String date) {
        return footballService.getMatchesByDate(date);
    }
    
    @GetMapping("/matches/{fixtureId}")
    public FootballMatchDetailsDto getMatchDetails(@PathVariable String fixtureId) {
        return footballService.getMatchDetails(fixtureId);
    }
    
    @GetMapping("/standings")
    public List<FootballStandingDto> getStandings(@RequestParam(defaultValue = "39") String league, @RequestParam(defaultValue = "2023") String season) {
        return footballService.getStandings(league, season);
    }
}
