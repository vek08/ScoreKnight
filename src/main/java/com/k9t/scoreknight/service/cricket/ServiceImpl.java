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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceImpl implements com.k9t.scoreknight.service.cricket.Service {

    private final CricApiClient cricApiClient;
    private final ObjectMapper objectMapper;

    @Override
    public HomeMatchesResponseDto getHomeMatches() {
        try {
            Map<String, CricketMatchCardDto> liveMatches = new LinkedHashMap<>();
            Map<String, CricketMatchCardDto> upcomingMatches = new LinkedHashMap<>();
            Map<String, CricketMatchCardDto> completedMatches = new LinkedHashMap<>();
            Map<String, CricketMatchCardDto> allProcessed = new LinkedHashMap<>();

            // 1. Fetch current matches first
            try {
                String currentResponse = cricApiClient.getCurrentMatches();
                processMatchesResponse(currentResponse, liveMatches, upcomingMatches, completedMatches, allProcessed);
            } catch (Exception e) {
                // Log and continue
                System.err.println("Error fetching current matches: " + e.getMessage());
            }

            // 2. Fetch all matches with pagination
            int offset = 0;
            int fetched = 0;
            while (true) {
                String response = cricApiClient.getMatches(offset);
                JsonNode root = objectMapper.readTree(response);
                
                if (root.has("status") && "failure".equalsIgnoreCase(root.get("status").asText())) {
                    String reason = root.has("reason") ? root.get("reason").asText() : "Unknown API error";
                    if (allProcessed.isEmpty()) {
                        throw new RuntimeException("CricAPI error: " + reason);
                    }
                    break;
                }

                JsonNode data = root.get("data");
                if (data == null || !data.isArray() || data.isEmpty()) {
                    break;
                }

                processMatchesResponse(response, liveMatches, upcomingMatches, completedMatches, allProcessed);

                fetched += data.size();
                
                JsonNode info = root.get("info");
                if (info != null && info.has("offsetRows") && info.has("totalRows")) {
                    int offsetRows = info.get("offsetRows").asInt();
                    int totalRows = info.get("totalRows").asInt();
                    offset += offsetRows > 0 ? offsetRows : data.size();
                    if (offset >= totalRows || offsetRows == 0) {
                        break;
                    }
                } else {
                    offset += data.size();
                }

                // Prevent excessive API calls if totalRows is huge (max 200 matches)
                if (fetched >= 200) {
                    break;
                }
            }

            if (allProcessed.isEmpty()) {
                throw new RuntimeException("No valid matches could be fetched");
            }

            return HomeMatchesResponseDto.builder()
                    .liveMatches(new ArrayList<>(liveMatches.values()))
                    .upcomingMatches(new ArrayList<>(upcomingMatches.values()))
                    .completedMatches(new ArrayList<>(completedMatches.values()))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "Failed to parse cricket API response", e);
        }
    }

    private void processMatchesResponse(String response, 
                                        Map<String, CricketMatchCardDto> liveMatches, 
                                        Map<String, CricketMatchCardDto> upcomingMatches, 
                                        Map<String, CricketMatchCardDto> completedMatches,
                                        Map<String, CricketMatchCardDto> allProcessed) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode data = root.get("data");
        
        if (data != null && data.isArray()) {
            for (JsonNode matchNode : data) {
                CricketMatchCardDto dto = parseMatch(matchNode);
                if (dto == null || dto.getMatchId() == null) continue;
                
                String matchId = dto.getMatchId();
                if (allProcessed.containsKey(matchId)) continue;
                
                allProcessed.put(matchId, dto);
                
                boolean hasMatchStarted = matchNode.has("matchStarted");
                boolean hasMatchEnded = matchNode.has("matchEnded");
                
                if (hasMatchStarted && hasMatchEnded) {
                    boolean matchStarted = matchNode.get("matchStarted").asBoolean(false);
                    boolean matchEnded = matchNode.get("matchEnded").asBoolean(false);
                    
                    if (matchEnded) {
                        completedMatches.put(matchId, dto);
                    } else if (!matchStarted) {
                        upcomingMatches.put(matchId, dto);
                    } else {
                        liveMatches.put(matchId, dto);
                    }
                } else {
                    String status = dto.getStatus() != null ? dto.getStatus().toLowerCase() : "";
                    if (status.contains("won") || status.contains("abandoned") || status.contains("tied") || status.contains("no result") || status.contains("draw")) {
                        completedMatches.put(matchId, dto);
                    } else if (status.contains("live") || status.contains("inning") || status.contains("stump") || status.contains("tea") || status.contains("lunch") || status.contains("rain") || status.contains("bad light")) {
                        liveMatches.put(matchId, dto);
                    } else if (status.contains("upcoming") || status.contains("yet to begin") || status.contains("toss") || status.contains("starts")) {
                        upcomingMatches.put(matchId, dto);
                    } else {
                        completedMatches.put(matchId, dto); // Fallback
                    }
                }
            }
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
        if (match.has("matchType")) dto.setMatchType(match.get("matchType").asText());

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
