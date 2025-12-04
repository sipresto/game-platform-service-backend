package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.AchievementEvaluationRule;

import java.util.*;

public class AchievementRules {

    // Tic Tac Toe: Win once as X and once as O
    public static final AchievementEvaluationRule WIN_AS_BOTH_SIDES = (context, progress) -> {
        @SuppressWarnings("unchecked")
        Set<String> sidesWon = new HashSet<>(
                (Collection<String>) progress.getMetadata().getOrDefault("sidesWon", Collections.emptySet())
        );

        Optional<String> sideOpt = context.getGameData("playerSide", String.class);
        if (sideOpt.isEmpty()) {
            return AchievementEvaluationRule.EvaluationResult.skip();
        }
        String side = sideOpt.get();
        boolean added = sidesWon.add(side);

        if (added) {
            return AchievementEvaluationRule.EvaluationResult.countWithMetadata(1, Map.of("sidesWon", sidesWon));
        }

        return AchievementEvaluationRule.EvaluationResult.skip();
    };

    // Quoridor: Win a game where you used less walls than your opponent
    public static final AchievementEvaluationRule EFFICIENT_WIN = (context, progress) -> {
        Integer playerWalls = context.getGameData("playerWallsUsed", Integer.class).orElse(null);
        Integer opponentWalls = context.getGameData("opponentWallsUsed", Integer.class).orElse(null);

        if (playerWalls != null && opponentWalls != null && playerWalls < opponentWalls) {
            return AchievementEvaluationRule.EvaluationResult.count(1);
        }

        return AchievementEvaluationRule.EvaluationResult.skip();
    };

    // Quoridor: Win a game where both players used all their walls
    public static final AchievementEvaluationRule MAZE_MASTER = (context, progress) -> {
        Integer playerWalls = context.getGameData("playerWallsUsed", Integer.class).orElse(null);
        Integer opponentWalls = context.getGameData("opponentWallsUsed", Integer.class).orElse(null);
        Integer maxWalls = context.getGameData("maxWallsPerPlayer", Integer.class).orElse(10);

        if (playerWalls != null && opponentWalls != null
                && playerWalls.equals(maxWalls) && opponentWalls.equals(maxWalls)) {
            return AchievementEvaluationRule.EvaluationResult.count(1);
        }

        return AchievementEvaluationRule.EvaluationResult.skip();
    };
}
