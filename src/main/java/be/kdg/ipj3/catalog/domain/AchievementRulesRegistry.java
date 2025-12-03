package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.AchievementEvaluationRule;

import java.util.Map;

public class AchievementRulesRegistry {
    private static final Map<String, AchievementEvaluationRule> RULES = Map.of(
            "WIN_AS_BOTH_SIDES", AchievementRules.WIN_AS_BOTH_SIDES,
            "EFFICIENT_WIN", AchievementRules.EFFICIENT_WIN,
            "MAZE_MASTER", AchievementRules.MAZE_MASTER
    );

    public static AchievementEvaluationRule get(String key) {
        return RULES.get(key);
    }
}
