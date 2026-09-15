package com.k9t.scoreknight.service.cricket;

import com.k9t.scoreknight.dto.cricket.CricketMatchCardDto;
import com.k9t.scoreknight.dto.cricket.HomeMatchesResponseDto;

public interface Service {
    HomeMatchesResponseDto getHomeMatches();
    CricketMatchCardDto getMatchDetails(String matchId);
}

