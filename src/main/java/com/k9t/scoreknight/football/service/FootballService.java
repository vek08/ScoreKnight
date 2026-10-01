package com.k9t.scoreknight.football.service;

import com.k9t.scoreknight.football.dto.HomeFootballMatchesResponseDto;
import com.k9t.scoreknight.football.dto.FootballMatchCardDto;
import java.util.List;
import com.k9t.scoreknight.football.dto.FootballMatchDetailsDto;
import com.k9t.scoreknight.football.dto.FootballStandingDto;

public interface FootballService {
    HomeFootballMatchesResponseDto getHomeMatches();
    List<FootballMatchCardDto> getMatchesByDate(String date);
    FootballMatchDetailsDto getMatchDetails(String fixtureId);
    List<FootballStandingDto> getStandings(String league, String season);
}
