package com.k9t.scoreknight.service.cricket;

import com.k9t.scoreknight.client.CricApiClient;
import com.k9t.scoreknight.dto.cricket.CricketMatchCardDto;
import com.k9t.scoreknight.dto.cricket.HomeMatchesResponseDto;
import com.k9t.scoreknight.dto.cricket.MatchPlayerDto;
import com.k9t.scoreknight.dto.cricket.MatchSquadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceImpl implements com.k9t.scoreknight.service.cricket.Service {

    private final CricApiClient cricApiClient;
    private final ObjectMapper objectMapper;

    @Override
    public HomeMatchesResponseDto getHomeMatches() {
        try {
            String response = cricApiClient.getCurrentMatches();
            JsonNode root = objectMapper.readTree(response);
            
            // Check for API failure
            if (root.has("status") && "failure".equalsIgnoreCase(root.get("status").asText())) {
                String reason = root.has("reason") ? root.get("reason").asText() : "Unknown API error";
                throw new RuntimeException("CricAPI error: " + reason);
            }
            
            List<CricketMatchCardDto> liveMatches = new ArrayList<>();
            List<CricketMatchCardDto> upcomingMatches = new ArrayList<>();
            List<CricketMatchCardDto> completedMatches = new ArrayList<>();

            JsonNode data = root.get("data");
            if (data != null && data.isArray()) {
                for (JsonNode matchNode : data) {
                    CricketMatchCardDto dto = parseMatch(matchNode);
                    if (dto == null) continue;
                    
                    String status = dto.getStatus() != null ? dto.getStatus().toLowerCase() : "";
                    String matchType = matchNode.has("matchType") ? matchNode.get("matchType").asText().toLowerCase() : "";
                    String date = dto.getMatchDate() != null ? dto.getMatchDate() : "";
                    
                    if (status.contains("live") || status.contains("inning") || status.contains("stump") || status.contains("tea") || status.contains("lunch") || status.contains("rain") || status.contains("bad light")) {
                        liveMatches.add(dto);
                    } else if (status.contains("upcoming") || status.contains("yet to begin") || status.contains("toss") || status.contains("starts")) {
                        upcomingMatches.add(dto);
                    } else if (status.contains("won") || status.contains("abandoned") || status.contains("tied") || status.contains("no result") || status.contains("draw")) {
                        completedMatches.add(dto);
                    } else if (status.isEmpty() && matchNode.has("matchStarted") && !matchNode.get("matchStarted").asBoolean(false)) {
                        upcomingMatches.add(dto);
                    } else {
                        completedMatches.add(dto);
                    }
                }
            }

            return HomeMatchesResponseDto.builder()
                    .liveMatches(liveMatches)
                    .upcomingMatches(upcomingMatches)
                    .completedMatches(completedMatches)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "Failed to parse cricket API response", e);
        }
    }

    @Override
    public CricketMatchCardDto getMatchDetails(String matchId) {
        try {
            String response = cricApiClient.getMatchInfo(matchId);
            JsonNode root = objectMapper.readTree(response);
            
            if (root.has("status") && "failure".equalsIgnoreCase(root.get("status").asText())) {
                String reason = root.has("reason") ? root.get("reason").asText() : "Unknown API error";
                throw new RuntimeException("CricAPI error: " + reason);
            }
            
            JsonNode data = root.get("data");
            if (data != null) {
                CricketMatchCardDto dto = parseMatch(data);
                
                try {
                    String squadResponse = cricApiClient.getMatchSquad(matchId);
                    JsonNode squadRoot = objectMapper.readTree(squadResponse);
                    if (squadRoot.has("data") && squadRoot.get("data").isArray()) {
                        List<MatchSquadDto> squads = parseSquads(squadRoot.get("data"));
                        dto.setSquads(squads);
                    }
                } catch (Exception ignored) {
                    // Ignore squad fetch errors
                }
                
                return dto;
            }
            
            throw new RuntimeException("Match not found");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "Failed to fetch match details", e);
        }
    }
    
    private CricketMatchCardDto parseMatch(JsonNode match) {
        CricketMatchCardDto dto = new CricketMatchCardDto();
        if (match.has("id")) dto.setMatchId(match.get("id").asText());

        JsonNode teams = match.get("teamInfo");
        if (teams != null && teams.isArray() && teams.size() >= 2) {
            dto.setTeamA(teams.get(0).has("shortname") ? teams.get(0).get("shortname").asText() : (teams.get(0).has("name") ? teams.get(0).get("name").asText() : ""));
            dto.setTeamB(teams.get(1).has("shortname") ? teams.get(1).get("shortname").asText() : (teams.get(1).has("name") ? teams.get(1).get("name").asText() : ""));
            dto.setTeamALogo(teams.get(0).has("img") ? teams.get(0).get("img").asText() : null);
            dto.setTeamBLogo(teams.get(1).has("img") ? teams.get(1).get("img").asText() : null);
        } else if (match.has("teams") && match.get("teams").isArray() && match.get("teams").size() >= 2) {
            dto.setTeamA(match.get("teams").get(0).asText());
            dto.setTeamB(match.get("teams").get(1).asText());
        }

        if (match.has("status")) dto.setStatus(match.get("status").asText());
        if (match.has("venue")) dto.setVenue(match.get("venue").asText());
        if (match.has("date")) dto.setMatchDate(match.get("date").asText());

        if (match.has("score")) {
            JsonNode scoreNode = match.get("score");
            if (scoreNode.isArray() && !scoreNode.isEmpty()) {
                JsonNode firstInning = scoreNode.get(0);
                if (firstInning.has("r") && firstInning.has("w") && firstInning.has("o")) {
                    dto.setScore(firstInning.get("r").asText() + "/" + firstInning.get("w").asText() + " (" + firstInning.get("o").asText() + ")");
                }
            } else if (scoreNode.isObject()) {
                if (scoreNode.has("r") && scoreNode.has("w") && scoreNode.has("o")) {
                    dto.setScore(scoreNode.get("r").asText() + "/" + scoreNode.get("w").asText() + " (" + scoreNode.get("o").asText() + ")");
                }
            } else if (scoreNode.isTextual()) {
                dto.setScore(scoreNode.asText());
            }
        }
        return dto;
    }
    
    private List<MatchSquadDto> parseSquads(JsonNode data) {
        List<MatchSquadDto> squads = new ArrayList<>();
        for (JsonNode team : data) {
            MatchSquadDto squad = new MatchSquadDto();
            if (team.has("teamName")) squad.setTeamName(team.get("teamName").asText());
            if (team.has("img")) squad.setImg(team.get("img").asText());
            
            List<MatchPlayerDto> players = new ArrayList<>();
            if (team.has("players") && team.get("players").isArray()) {
                for (JsonNode p : team.get("players")) {
                    MatchPlayerDto player = new MatchPlayerDto();
                    if (p.has("id")) player.setId(p.get("id").asText());
                    if (p.has("name")) player.setName(p.get("name").asText());
                    if (p.has("role")) player.setRole(p.get("role").asText());
                    if (p.has("battingStyle")) player.setBattingStyle(p.get("battingStyle").asText());
                    if (p.has("bowlingStyle")) player.setBowlingStyle(p.get("bowlingStyle").asText());
                    if (p.has("country")) player.setCountry(p.get("country").asText());
                    if (p.has("playerImg")) player.setPlayerImg(p.get("playerImg").asText());
                    players.add(player);
                }
            }
            squad.setPlayers(players);
            squads.add(squad);
        }
        return squads;
    }
}

