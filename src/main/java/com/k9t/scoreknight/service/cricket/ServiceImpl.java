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
            throw new RuntimeException("Failed to parse cricket API response", e);
        }
    }
    
    @Override
    public CricketMatchCardDto getMatchDetails(String matchId) {
        try {
            String response = cricApiClient.getMatchInfo(matchId);
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");
            if (data != null) {
                CricketMatchCardDto dto = parseMatch(data);
                
                try {
                    String squadResponse = cricApiClient.getMatchSquad(matchId);
                    JsonNode squadRoot = objectMapper.readTree(squadResponse);
                    if (squadRoot.has("data") && squadRoot.get("data").isArray()) {
                        dto.setSquads(parseSquads(squadRoot.get("data")));
                    }
                } catch (Exception e) {
                    System.err.println("Squad fetch failed for match " + matchId);
                }
                
                return dto;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch match details", e);
        }
    }

    private List<MatchSquadDto> parseSquads(JsonNode data) {
        List<MatchSquadDto> squads = new ArrayList<>();
        for (JsonNode teamNode : data) {
            MatchSquadDto squad = new MatchSquadDto();
            if (teamNode.has("teamName")) squad.setTeamName(teamNode.get("teamName").asText());
            if (teamNode.has("img")) squad.setImg(teamNode.get("img").asText());
            
            List<MatchPlayerDto> playersList = new ArrayList<>();
            if (teamNode.has("players") && teamNode.get("players").isArray()) {
                for (JsonNode playerNode : teamNode.get("players")) {
                    MatchPlayerDto player = new MatchPlayerDto();
                    if (playerNode.has("id")) player.setId(playerNode.get("id").asText());
                    if (playerNode.has("name")) player.setName(playerNode.get("name").asText());
                    if (playerNode.has("role")) player.setRole(playerNode.get("role").asText());
                    if (playerNode.has("battingStyle")) player.setBattingStyle(playerNode.get("battingStyle").asText());
                    if (playerNode.has("bowlingStyle")) player.setBowlingStyle(playerNode.get("bowlingStyle").asText());
                    if (playerNode.has("country")) player.setCountry(playerNode.get("country").asText());
                    if (playerNode.has("playerImg")) player.setPlayerImg(playerNode.get("playerImg").asText());
                    playersList.add(player);
                }
            }
            squad.setPlayers(playersList);
            squads.add(squad);
        }
        return squads;
    }

    private CricketMatchCardDto parseMatch(JsonNode match) {
        if (match == null) return null;
        CricketMatchCardDto dto = new CricketMatchCardDto();

        if (match.has("id")) {
            dto.setMatchId(match.get("id").asText());
        }

        JsonNode teams = match.get("teams");
        if (teams != null && teams.isArray() && teams.size() >= 2) {
            dto.setTeamA(teams.get(0).asText());
            dto.setTeamB(teams.get(1).asText());
        }

        JsonNode teamInfo = match.get("teamInfo");
        if (teamInfo != null && teamInfo.isArray()) {
            if (!teamInfo.isEmpty() && teamInfo.get(0).has("img")) {
                dto.setTeamALogo(teamInfo.get(0).get("img").asText());
            }
            if (teamInfo.size() >= 2 && teamInfo.get(1).has("img")) {
                dto.setTeamBLogo(teamInfo.get(1).get("img").asText());
            }
        }

        if (match.has("status")) {
            dto.setStatus(match.get("status").asText());
        }

        if (match.has("venue")) {
            dto.setVenue(match.get("venue").asText());
        }

        if (match.has("date")) {
            dto.setMatchDate(match.get("date").asText());
        }

        if (match.has("score")) {
            dto.setScore(match.get("score").toString());
        }

        return dto;
    }
}

