package com.k9t.scoreknight.football.dto.api;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiStandingResponse {
    private ApiLeague league;
    public ApiLeague getLeague() { return league; }
    public void setLeague(ApiLeague league) { this.league = league; }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiLeague {
        private List<List<ApiStandingItem>> standings;
        public List<List<ApiStandingItem>> getStandings() { return standings; }
        public void setStandings(List<List<ApiStandingItem>> standings) { this.standings = standings; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiStandingItem {
        private Integer rank;
        private ApiTeam team;
        private Integer points;
        private Integer goalsDiff;
        private String form;
        private ApiGames all;
        
        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
        public ApiTeam getTeam() { return team; }
        public void setTeam(ApiTeam team) { this.team = team; }
        public Integer getPoints() { return points; }
        public void setPoints(Integer points) { this.points = points; }
        public Integer getGoalsDiff() { return goalsDiff; }
        public void setGoalsDiff(Integer goalsDiff) { this.goalsDiff = goalsDiff; }
        public String getForm() { return form; }
        public void setForm(String form) { this.form = form; }
        public ApiGames getAll() { return all; }
        public void setAll(ApiGames all) { this.all = all; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiTeam {
        private String name;
        private String logo;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLogo() { return logo; }
        public void setLogo(String logo) { this.logo = logo; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiGames {
        private Integer played;
        private Integer win;
        private Integer draw;
        private Integer lose;
        public Integer getPlayed() { return played; }
        public void setPlayed(Integer played) { this.played = played; }
        public Integer getWin() { return win; }
        public void setWin(Integer win) { this.win = win; }
        public Integer getDraw() { return draw; }
        public void setDraw(Integer draw) { this.draw = draw; }
        public Integer getLose() { return lose; }
        public void setLose(Integer lose) { this.lose = lose; }
    }
}
