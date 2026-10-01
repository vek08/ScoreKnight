package com.k9t.scoreknight.football.dto;

public class FootballEventDto {
    private String timeElapsed;
    private String teamName;
    private String teamLogo;
    private String playerName;
    private String assistName;
    private String type;
    private String detail;

    public String getTimeElapsed() { return timeElapsed; }
    public void setTimeElapsed(String timeElapsed) { this.timeElapsed = timeElapsed; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamLogo() { return teamLogo; }
    public void setTeamLogo(String teamLogo) { this.teamLogo = teamLogo; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getAssistName() { return assistName; }
    public void setAssistName(String assistName) { this.assistName = assistName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}
