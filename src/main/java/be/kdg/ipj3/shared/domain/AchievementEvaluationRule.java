package be.kdg.ipj3.shared.domain;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.achievements.domain.GameContext;

import java.util.Map;

@FunctionalInterface
public interface AchievementEvaluationRule {
    EvaluationResult evaluate(GameContext context, AchievementProgress currentProgress);

    record EvaluationResult(boolean shouldCount, int incrementAmount, Map<String, Object> metadata) {
        public static EvaluationResult count(int amount) {
            return new EvaluationResult(true, amount, null);
        }

        public static EvaluationResult countWithMetadata(int amount, Map<String, Object> metadata) {
            return new EvaluationResult(true, amount, metadata);
        }

        public static EvaluationResult skip() {
            return new EvaluationResult(false, 0, null);
        }
    }
}
