package net.kagaries.prototypepaincompats.custom.thought;

import net.adinvas.prototype_pain.client.moodles.MoodleStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ThoughtMain {
    public static void sendThought(Player player, ThoughtType thoughtType, Component text) {
        player.displayClientMessage(text.copy().withStyle(thoughtType.getChatFormatting()), true);
    }

    public static void sendMoodleThought(
            Player player,
            String moodleName,
            MoodleStatus newStatus,
            MoodleStatus lastStatus
    ) {
        if (newStatus.ordinal() <= lastStatus.ordinal()) {
            return;
        }

        Component message = switch (newStatus) {
            case LIGHT -> Component.translatable("prototype_pain_compats.thought." + moodleName + ".light");
            case NORMAL -> Component.translatable("prototype_pain_compats.thought." + moodleName + ".normal");
            case HEAVY -> Component.translatable("prototype_pain_compats.thought." + moodleName + ".heavy");
            case CRITICAL -> Component.translatable("prototype_pain_compats.thought." + moodleName +".critical");
            default -> null;
        };

        ThoughtType type = switch (newStatus) {
            case LIGHT -> ThoughtType.Normal;
            case NORMAL -> ThoughtType.Uncertain;
            case HEAVY -> ThoughtType.Bad;
            case CRITICAL -> ThoughtType.Horrible;
            default -> null;
        };

        if (message != null) {
            sendThought(player, type, message);
        }
    }
}
