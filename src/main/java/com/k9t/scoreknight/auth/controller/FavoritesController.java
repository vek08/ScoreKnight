package com.k9t.scoreknight.auth.controller;
import com.k9t.scoreknight.auth.dto.FavoriteDto;
import com.k9t.scoreknight.auth.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesController {
    private final FavoritesService favoritesService;

    @GetMapping
    public ResponseEntity<List<FavoriteDto>> getFavorites() {
        return ResponseEntity.ok(favoritesService.getFavorites());
    }

    @PostMapping
    public ResponseEntity<Void> addFavorite(@RequestBody FavoriteDto dto) {
        favoritesService.addFavorite(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@RequestParam String matchId, @RequestParam String sportType) {
        favoritesService.removeFavorite(matchId, sportType);
        return ResponseEntity.ok().build();
    }
}
