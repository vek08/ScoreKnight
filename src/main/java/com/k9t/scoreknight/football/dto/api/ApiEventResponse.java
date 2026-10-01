package com.k9t.scoreknight.football.dto.api;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiEventResponse {
    private ApiTime time;
    private ApiTeam team;
    private ApiPlayer player;
    private ApiPlayer assist;
    private String type;
    private String detail;
    private String comments;

    public ApiTime getTime() { return time; }
    public void setTime(ApiTime time) { this.time = time; }
    public ApiTeam getTeam() { return team; }
    public void setTeam(ApiTeam team) { this.team = team; }
    public ApiPlayer getPlayer() { return player; }
    public void setPlayer(ApiPlayer player) { this.player = player; }
    public ApiPlayer getAssist() { return assist; }
    public void setAssist(ApiPlayer assist) { this.assist = assist; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiTime {
        private Integer elapsed;
        private Integer extra;
        public Integer getElapsed() { return elapsed; }
        public void setElapsed(Integer elapsed) { this.elapsed = elapsed; }
        public Integer getExtra() { return extra; }
        public void setExtra(Integer extra) { this.extra = extra; }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiTeam {
        private Long id;
        private String name;
        private String logo;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLogo() { return logo; }
        public void setLogo(String logo) { this.logo = logo; }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiPlayer {
        private Long id;
        private String name;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
