package com.k9t.scoreknight.football.dto;

import java.util.List;

public class FootballMatchDetailsDto {
    private FootballMatchCardDto match;
    private List<FootballEventDto> events;

    public FootballMatchCardDto getMatch() { return match; }
    public void setMatch(FootballMatchCardDto match) { this.match = match; }
    public List<FootballEventDto> getEvents() { return events; }
    public void setEvents(List<FootballEventDto> events) { this.events = events; }
}
