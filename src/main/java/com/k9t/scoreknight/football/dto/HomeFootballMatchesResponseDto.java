package com.k9t.scoreknight.football.dto;

import java.util.List;

public class HomeFootballMatchesResponseDto {
    private List<FootballMatchCardDto> liveMatches;
    private List<FootballMatchCardDto> upcomingMatches;
    private List<FootballMatchCardDto> completedMatches;

    public List<FootballMatchCardDto> getLiveMatches() { return liveMatches; }
    public void setLiveMatches(List<FootballMatchCardDto> liveMatches) { this.liveMatches = liveMatches; }
    public List<FootballMatchCardDto> getUpcomingMatches() { return upcomingMatches; }
    public void setUpcomingMatches(List<FootballMatchCardDto> upcomingMatches) { this.upcomingMatches = upcomingMatches; }
    public List<FootballMatchCardDto> getCompletedMatches() { return completedMatches; }
    public void setCompletedMatches(List<FootballMatchCardDto> completedMatches) { this.completedMatches = completedMatches; }
}
