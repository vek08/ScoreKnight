package com.k9t.scoreknight.football.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiFixtureResponse {
    private ApiFixture fixture;
    private ApiLeague league;
    private ApiTeams teams;
    private ApiGoals goals;

    public ApiFixture getFixture() { return fixture; }
    public void setFixture(ApiFixture fixture) { this.fixture = fixture; }
    public ApiLeague getLeague() { return league; }
    public void setLeague(ApiLeague league) { this.league = league; }
    public ApiTeams getTeams() { return teams; }
    public void setTeams(ApiTeams teams) { this.teams = teams; }
    public ApiGoals getGoals() { return goals; }
    public void setGoals(ApiGoals goals) { this.goals = goals; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiFixture {
        private Long id;
        private String date;
        private ApiStatus status;
        private ApiVenue venue;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public ApiStatus getStatus() { return status; }
        public void setStatus(ApiStatus status) { this.status = status; }
        public ApiVenue getVenue() { return venue; }
        public void setVenue(ApiVenue venue) { this.venue = venue; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiStatus {
        private String short_;
        private Integer elapsed;
        
        public String getShort() { return short_; }
        public void setShort(String short_) { this.short_ = short_; }
        public Integer getElapsed() { return elapsed; }
        public void setElapsed(Integer elapsed) { this.elapsed = elapsed; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiVenue {
        private String name;
        private String city;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiLeague {
        private String name;
        private String logo;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLogo() { return logo; }
        public void setLogo(String logo) { this.logo = logo; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiTeams {
        private ApiTeam home;
        private ApiTeam away;
        
        public ApiTeam getHome() { return home; }
        public void setHome(ApiTeam home) { this.home = home; }
        public ApiTeam getAway() { return away; }
        public void setAway(ApiTeam away) { this.away = away; }
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
    public static class ApiGoals {
        private Integer home;
        private Integer away;
        
        public Integer getHome() { return home; }
        public void setHome(Integer home) { this.home = home; }
        public Integer getAway() { return away; }
        public void setAway(Integer away) { this.away = away; }
    }
}
