package com.k9t.scoreknight.auth.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorites")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private String matchId;
    private String sportType; // "CRICKET" or "FOOTBALL"
}
