package com.k9t.scoreknight.football.service;

import com.k9t.scoreknight.football.client.FootballApiClient;
import com.k9t.scoreknight.football.dto.FootballMatchCardDto;
import com.k9t.scoreknight.football.dto.HomeFootballMatchesResponseDto;
import com.k9t.scoreknight.football.dto.api.ApiFixtureResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.k9t.scoreknight.football.dto.FootballMatchDetailsDto;
import com.k9t.scoreknight.football.dto.FootballEventDto;
import com.k9t.scoreknight.football.dto.api.ApiEventResponse;
import com.k9t.scoreknight.football.dto.FootballStandingDto;
import com.k9t.scoreknight.football.dto.api.ApiStandingResponse;

@Service
public class FootballServiceImpl implements FootballService {

    private final FootballApiClient apiClient;

    public FootballServiceImpl(FootballApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public HomeFootballMatchesResponseDto getHomeMatches() {
        HomeFootballMatchesResponseDto response = new HomeFootballMatchesResponseDto();
        
        List<FootballMatchCardDto> liveMatches = new ArrayList<>();
        try {
            var liveRes = apiClient.getLiveFixtures();
            if (liveRes != null && liveRes.getResponse() != null) {
                liveRes.getResponse().forEach(f -> liveMatches.add(mapToDto(f)));
            }
        } catch (Exception ignored) {}
        
        List<FootballMatchCardDto> upcomingMatches = new ArrayList<>();
        List<FootballMatchCardDto> completedMatches = new ArrayList<>();
        
        try {
            var todayRes = apiClient.getFixturesByDate(LocalDate.now().toString());
            if (todayRes != null && todayRes.getResponse() != null) {
                todayRes.getResponse().forEach(f -> {
                    String status = f.getFixture().getStatus().getShort();
                    if ("FT".equals(status) || "AET".equals(status) || "PEN".equals(status)) {
                        completedMatches.add(mapToDto(f));
                    } else if ("TBD".equals(status) || "NS".equals(status)) {
                        upcomingMatches.add(mapToDto(f));
                    }
                });
            }
        } catch (Exception ignored) {}
        
        response.setLiveMatches(liveMatches);
        response.setUpcomingMatches(upcomingMatches);
        response.setCompletedMatches(completedMatches);
        
        return response;
    }

    @Override
    public List<FootballMatchCardDto> getMatchesByDate(String date) {
        List<FootballMatchCardDto> result = new ArrayList<>();
        var res = apiClient.getFixturesByDate(date);
        if (res != null && res.getResponse() != null) {
            res.getResponse().forEach(f -> result.add(mapToDto(f)));
        }
        return result;
    }

    
    @Override
    public FootballMatchDetailsDto getMatchDetails(String fixtureId) {
        FootballMatchDetailsDto details = new FootballMatchDetailsDto();
        
        try {
            var fixRes = apiClient.getFixtureById(fixtureId);
            if (fixRes != null && fixRes.getResponse() != null && !fixRes.getResponse().isEmpty()) {
                details.setMatch(mapToDto(fixRes.getResponse().get(0)));
            }
            
            var eventsRes = apiClient.getFixtureEvents(fixtureId);
            List<FootballEventDto> events = new ArrayList<>();
            if (eventsRes != null && eventsRes.getResponse() != null) {
                eventsRes.getResponse().forEach(e -> {
                    FootballEventDto dto = new FootballEventDto();
                    if (e.getTime() != null) {
                        String t = e.getTime().getElapsed() != null ? e.getTime().getElapsed().toString() : "";
                        if (e.getTime().getExtra() != null) t += "+" + e.getTime().getExtra();
                        dto.setTimeElapsed(t + "'");
                    }
                    if (e.getTeam() != null) {
                        dto.setTeamName(e.getTeam().getName());
                        dto.setTeamLogo(e.getTeam().getLogo());
                    }
                    if (e.getPlayer() != null) dto.setPlayerName(e.getPlayer().getName());
                    if (e.getAssist() != null) dto.setAssistName(e.getAssist().getName());
                    dto.setType(e.getType());
                    dto.setDetail(e.getDetail());
                    events.add(dto);
                });
            }
            details.setEvents(events);
        } catch (Exception ignored) {}
        
        return details;
    }
    
    @Override
    public List<FootballStandingDto> getStandings(String league, String season) {
        List<FootballStandingDto> result = new ArrayList<>();
        try {
            var res = apiClient.getStandings(league, season);
            if (res != null && res.getResponse() != null && !res.getResponse().isEmpty()) {
                var l = res.getResponse().get(0).getLeague();
                if (l != null && l.getStandings() != null && !l.getStandings().isEmpty()) {
                    l.getStandings().get(0).forEach(s -> {
                        FootballStandingDto dto = new FootballStandingDto();
                        dto.setPosition(s.getRank() != null ? s.getRank().toString() : "");
                        if (s.getTeam() != null) {
                            dto.setTeamName(s.getTeam().getName());
                            dto.setTeamLogo(s.getTeam().getLogo());
                        }
                        if (s.getAll() != null) {
                            dto.setPlayed(s.getAll().getPlayed() != null ? s.getAll().getPlayed().toString() : "0");
                            dto.setWon(s.getAll().getWin() != null ? s.getAll().getWin().toString() : "0");
                            dto.setDrawn(s.getAll().getDraw() != null ? s.getAll().getDraw().toString() : "0");
                            dto.setLost(s.getAll().getLose() != null ? s.getAll().getLose().toString() : "0");
                        }
                        dto.setGoalDifference(s.getGoalsDiff() != null ? s.getGoalsDiff().toString() : "0");
                        dto.setPoints(s.getPoints() != null ? s.getPoints().toString() : "0");
                        result.add(dto);
                    });
                }
            }
        } catch (Exception ignored) {}
        return result;
    }
    private FootballMatchCardDto mapToDto(ApiFixtureResponse f) {
        FootballMatchCardDto dto = new FootballMatchCardDto();
        if (f.getFixture() != null) {
            dto.setId(f.getFixture().getId());
            dto.setMatchDate(f.getFixture().getDate());
            if (f.getFixture().getStatus() != null) {
                String shortStatus = f.getFixture().getStatus().getShort();
                dto.setStatus(shortStatus);
                if (f.getFixture().getStatus().getElapsed() != null) {
                    dto.setElapsed(f.getFixture().getStatus().getElapsed().toString() + "'");
                }
            }
            if (f.getFixture().getVenue() != null) {
                dto.setVenue(f.getFixture().getVenue().getName() + ", " + f.getFixture().getVenue().getCity());
            }
        }
        if (f.getLeague() != null) {
            dto.setCompetition(f.getLeague().getName());
            dto.setCompetitionLogo(f.getLeague().getLogo());
        }
        if (f.getTeams() != null) {
            if (f.getTeams().getHome() != null) {
                dto.setHomeTeam(f.getTeams().getHome().getName());
                dto.setHomeTeamLogo(f.getTeams().getHome().getLogo());
            }
            if (f.getTeams().getAway() != null) {
                dto.setAwayTeam(f.getTeams().getAway().getName());
                dto.setAwayTeamLogo(f.getTeams().getAway().getLogo());
            }
        }
        if (f.getGoals() != null) {
            dto.setHomeScore(f.getGoals().getHome() != null ? f.getGoals().getHome().toString() : "0");
            dto.setAwayScore(f.getGoals().getAway() != null ? f.getGoals().getAway().toString() : "0");
        }
        return dto;
    }
}
