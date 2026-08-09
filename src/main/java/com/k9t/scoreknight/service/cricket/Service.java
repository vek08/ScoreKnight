package com.k9t.scoreknight.service.cricket;

import com.k9t.scoreknight.dto.cricket.CricketMatchCardDto;

import java.util.List;

public interface Service {
    List<CricketMatchCardDto> getHomeMatches();
}
