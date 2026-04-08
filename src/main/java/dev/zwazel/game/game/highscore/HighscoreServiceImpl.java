package dev.zwazel.game.game.highscore;

import dev.zwazel.game.user.User;
import dev.zwazel.game.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HighscoreServiceImpl implements HighscoreService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void updateHighscoreIfHigher(String email, int score) {
        if (email == null || email.isBlank()) {
            return;
        }

        userRepository.findUserByEmail(email).ifPresent(user -> {
            if (score > user.getHighScore()) {
                user.setHighScore(score);
                userRepository.save(user);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public int getHighscoreValue(String email) {
        if (email == null || email.isBlank()) {
            return 0;
        }

        return userRepository.findUserByEmail(email)
                .map(User::getHighScore)
                .orElse(0);
    }

    @Override
    @Transactional(readOnly = true)
    public OwnHighscoreResponse getOwnHighscore(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return new OwnHighscoreResponse(user.getEmail(), user.getHighScore());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboard(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        List<User> users = userRepository.findByOrderByHighScoreDesc(PageRequest.of(0, safeLimit));

        List<LeaderboardEntryResponse> leaderboard = new ArrayList<>(users.size());
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            leaderboard.add(new LeaderboardEntryResponse(i + 1, aliasFromEmail(user.getEmail()), user.getHighScore()));
        }
        return leaderboard;
    }

    private String aliasFromEmail(String email) {
        if (email == null || email.isBlank()) {
            return "Pilot";
        }

        int atIndex = email.indexOf('@');
        String localPart = atIndex > 0 ? email.substring(0, atIndex) : email;
        String trimmed = localPart.trim();
        if (trimmed.isEmpty()) {
            return "Pilot";
        }

        String prefix = trimmed.substring(0, Math.min(2, trimmed.length()));
        return prefix + "***";
    }
}

