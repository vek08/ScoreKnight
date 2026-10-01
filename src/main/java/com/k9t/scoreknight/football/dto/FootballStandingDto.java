package com.k9t.scoreknight.football.dto;

public class FootballStandingDto {
    private String position;
    private String teamName;
    private String teamLogo;
    private String played;
    private String won;
    private String drawn;
    private String lost;
    private String goalDifference;
    private String points;

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamLogo() { return teamLogo; }
    public void setTeamLogo(String teamLogo) { this.teamLogo = teamLogo; }
    public String getPlayed() { return played; }
    public void setPlayed(String played) { this.played = played; }
    public String getWon() { return won; }
    public void setWon(String won) { this.won = won; }
    public String getDrawn() { return drawn; }
    public void setDrawn(String drawn) { this.drawn = drawn; }
    public String getLost() { return lost; }
    public void setLost(String lost) { this.lost = lost; }
    public String getGoalDifference() { return goalDifference; }
    public void setGoalDifference(String goalDifference) { this.goalDifference = goalDifference; }
    public String getPoints() { return points; }
    public void setPoints(String points) { this.points = points; }
}
