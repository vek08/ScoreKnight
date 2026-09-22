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
            int fetched = 0;

            // 1. Fetch current matches first
            try {
                String currentResponse = cricApiClient.getCurrentMatches();
                processMatchesResponse(currentResponse, liveMatches, upcomingMatches, completedMatches, allProcessed);
                JsonNode root = objectMapper.readTree(currentResponse);
                if (root.has("data") && root.get("data").isArray()) {
                    fetched += root.get("data").size();
                }
            } catch (Exception e) {
                // Log and continue
                System.err.println("Error fetching current matches: " + e.getMessage());
                System.err.println("Failed to fetch currentMatches: " + e.getMessage());
            }

            // 2. Fetch paginated match listing for upcoming/completed
            int offset = 0;
            while (true) {
                String response;
                try {
                    response = cricApiClient.getMatches(offset);
                } catch (Exception e) {
                    System.err.println("Failed to fetch matches at offset " + offset + ": " + e.getMessage());
                    break;
                }

                JsonNode root = objectMapper.readTree(response);
                JsonNode data = root.get("data");
                if (data == null || !data.isArray() || data.isEmpty()) {
                    break;
                }

                processMatchesResponse(response, liveMatches, upcomingMatches, completedMatches, allProcessed);
                fetched += data.size();
                
                JsonNode info = root.get("info");
                if (info != null && info.has("totalRows")) {
                    int totalRows = info.get("totalRows").asInt();
                    offset += data.size();
                    if (offset >= totalRows) {
                        break;
                    }
                } else {
                    offset += data.size();
                }
                
                // Prevent API quota exhaustion (free tier limit is 100 hits/day)
                // Fetching up to 100 matches takes ~4 API hits.
                if (offset > 100) break;
            }

            if (allProcessed.isEmpty()) {
                throw new RuntimeException("No valid matches could be fetched");
            }

            System.out.println("Total API matches = " + fetched);
            System.out.println("Parsed matches = " + allProcessed.size());
            System.out.println("Live matches = " + liveMatches.size());
            System.out.println("Upcoming matches = " + upcomingMatches.size());
            System.out.println("Completed matches = " + completedMatches.size());
            
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
            System.out.println("API total matches: " + data.size());
            
            for (JsonNode matchNode : data) {
                System.out.println("matchId: " + (matchNode.has("id") ? matchNode.get("id").asText() : "null"));
                System.out.println("teams: " + (matchNode.has("teams") ? matchNode.get("teams").toString() : "null"));
                System.out.println("teamInfo: " + (matchNode.has("teamInfo") ? matchNode.get("teamInfo").toString() : "null"));
                System.out.println("status: " + (matchNode.has("status") ? matchNode.get("status").asText() : "null"));
                System.out.println("matchStarted: " + (matchNode.has("matchStarted") ? matchNode.get("matchStarted").asText() : "null"));
                System.out.println("matchEnded: " + (matchNode.has("matchEnded") ? matchNode.get("matchEnded").asText() : "null"));
                System.out.println("date: " + (matchNode.has("date") ? matchNode.get("date").asText() : "null"));
                System.out.println("dateTimeGMT: " + (matchNode.has("dateTimeGMT") ? matchNode.get("dateTimeGMT").asText() : "null"));
                System.out.println("matchType: " + (matchNode.has("matchType") ? matchNode.get("matchType").asText() : "null"));
                System.out.println("score: " + (matchNode.has("score") ? matchNode.get("score").toString() : "null"));
                
                CricketMatchCardDto dto = parseMatch(matchNode);
                if (dto == null || dto.getMatchId() == null) continue;
                
                String matchId = dto.getMatchId();
                if (allProcessed.containsKey(matchId)) continue;
                
                allProcessed.put(matchId, dto);
                
                boolean hasMatchStarted = matchNode.has("matchStarted");
                boolean hasMatchEnded = matchNode.has("matchEnded");
                boolean matchStarted = hasMatchStarted && matchNode.get("matchStarted").asBoolean(false);
                boolean matchEnded = hasMatchEnded && matchNode.get("matchEnded").asBoolean(false);

                if (hasMatchEnded && matchEnded) {
                    completedMatches.put(matchId, dto);
                } else if (hasMatchStarted && !matchStarted) {
                    upcomingMatches.put(matchId, dto);
                } else if (hasMatchStarted && matchStarted && hasMatchEnded && !matchEnded) {
                    liveMatches.put(matchId, dto);
                } else {
                    String status = dto.getStatus() != null ? dto.getStatus().toLowerCase() : "";
                    if (status.contains("won") || status.contains("abandoned") || status.contains("tied") || status.contains("no result") || status.contains("draw")) {
                        completedMatches.put(matchId, dto);
                    } else if (status.contains("live") || status.contains("inning") || status.contains("stump") || status.contains("tea") || status.contains("lunch") || status.contains("rain") || status.contains("bad light")) {
                        liveMatches.put(matchId, dto);
                    } else if (status.contains("upcoming") || status.contains("yet to begin") || status.contains("toss") || status.contains("starts") || status.contains("to begin")) {
                        upcomingMatches.put(matchId, dto);
                    } else {
                        // Do not classify as completed just because status is unknown.
                        // Default to upcoming or live based on if we have a score
                        if (dto.getScore() != null && !dto.getScore().isEmpty()) {
                            liveMatches.put(matchId, dto);
                        } else {
                            upcomingMatches.put(matchId, dto);
                        }
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
        } else if (match.has("name")) {
            String name = match.get("name").asText();
            if (name.contains(" vs ")) {
                String[] parts = name.split(" vs ");
                if (parts.length >= 2) {
                    dto.setTeamA(parts[0].trim());
                    dto.setTeamB(parts[1].split(",")[0].trim());
                }
            }
        }

        if (match.has("status")) dto.setStatus(match.get("status").asText());
        if (match.has("venue")) dto.setVenue(match.get("venue").asText());
        if (match.has("dateTimeGMT")) {
            dto.setMatchDate(match.get("dateTimeGMT").asText());
        } else if (match.has("date")) {
            dto.setMatchDate(match.get("date").asText());
        }
        if (match.has("matchType")) dto.setMatchType(match.get("matchType").asText());

        if (match.has("score")) {
            JsonNode scoreNode = match.get("score");
            dto.setScore(scoreNode.toString());
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
