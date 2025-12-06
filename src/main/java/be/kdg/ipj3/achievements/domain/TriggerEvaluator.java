package be.kdg.ipj3.achievements.domain;

import be.kdg.ipj3.shared.domain.CountTrigger;
import org.springframework.stereotype.Component;

@Component
public class TriggerEvaluator {

    public boolean shouldTrigger(CountTrigger trigger, GameContext context) {
        return switch (trigger) {
            case WIN_PLAYER -> context.isWinner() && !context.isAgainstAI();
            case WIN_AI -> context.isWinner() && context.isAgainstAI();
            case WIN_ANY -> context.isWinner();
            case DRAW_PLAYER -> context.isDraw() && !context.isAgainstAI();
            case DRAW_AI -> context.isDraw() && context.isAgainstAI();
            case DRAW_ANY -> context.isDraw();
            case WIN_OR_DRAW_ANY -> context.isWinner() || context.isDraw();
            case PLAY_ANY -> true;
        };
    }
}
