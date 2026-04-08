package dev.zwazel.game.game.highscore;

import java.util.List;

public interface HighscoreService {
    void updateHighscoreIfHigher(String email, int score);

    int getHighscoreValue(String email);

    OwnHighscoreResponse getOwnHighscore(String email);

    List<LeaderboardEntryResponse> getLeaderboard(int limit);
}

