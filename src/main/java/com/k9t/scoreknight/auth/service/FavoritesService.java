package com.k9t.scoreknight.auth.service;
import com.k9t.scoreknight.auth.dto.FavoriteDto;
import com.k9t.scoreknight.auth.entity.Favorite;
import com.k9t.scoreknight.auth.entity.User;
import com.k9t.scoreknight.auth.repository.FavoriteRepository;
import com.k9t.scoreknight.auth.repository.UserRepository;
import com.k9t.scoreknight.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritesService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    public List<FavoriteDto> getFavorites() {
        User user = getCurrentUser();
        return favoriteRepository.findByUser(user).stream().map(f -> {
            FavoriteDto dto = new FavoriteDto();
            dto.setMatchId(f.getMatchId());
            dto.setSportType(f.getSportType());
            return dto;
        }).collect(Collectors.toList());
    }

    public void addFavorite(FavoriteDto dto) {
        User user = getCurrentUser();
        Optional<Favorite> existing = favoriteRepository.findByUserAndMatchIdAndSportType(user, dto.getMatchId(), dto.getSportType());
        if (existing.isEmpty()) {
            Favorite fav = Favorite.builder()
                .user(user)
                .matchId(dto.getMatchId())
                .sportType(dto.getSportType())
                .build();
            favoriteRepository.save(fav);
        }
    }

    public void removeFavorite(String matchId, String sportType) {
        User user = getCurrentUser();
        favoriteRepository.findByUserAndMatchIdAndSportType(user, matchId, sportType)
            .ifPresent(favoriteRepository::delete);
    }
}
