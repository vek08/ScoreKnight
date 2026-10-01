package com.k9t.scoreknight.auth.repository;
import com.k9t.scoreknight.auth.entity.Favorite;
import com.k9t.scoreknight.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    Optional<Favorite> findByUserAndMatchIdAndSportType(User user, String matchId, String sportType);
}
