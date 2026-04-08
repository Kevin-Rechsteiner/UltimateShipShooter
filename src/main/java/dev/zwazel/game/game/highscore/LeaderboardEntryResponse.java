package dev.zwazel.game.game.highscore;

public record LeaderboardEntryResponse(int rank, String displayName, int highScore) {
}

