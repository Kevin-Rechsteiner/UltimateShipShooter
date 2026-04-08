package dev.zwazel.game.game.highscore;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/highscore")
@RequiredArgsConstructor
public class HighscoreController {

    private final HighscoreService highscoreService;

    @GetMapping("/me")
    public ResponseEntity<OwnHighscoreResponse> getOwnHighscore(Authentication authentication) {
        OwnHighscoreResponse response = highscoreService.getOwnHighscore(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(highscoreService.getLeaderboard(limit));
    }
}

