package net.kagaries.prototypepaincompats.mixin;

import net.adinvas.prototype_pain.client.moodles.AbstractMoodleVisual;
import net.adinvas.prototype_pain.client.moodles.MoodleController;
import net.adinvas.prototype_pain.client.moodles.MoodleStatus;
import net.kagaries.prototypepaincompats.custom.moodles.TickableMoodle;
import net.kagaries.prototypepaincompats.custom.thought.ThoughtMain;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashMap;

@Mixin(MoodleController.class)
public class MoodleControllerMixin {
    @Unique
    private static HashMap<String, MoodleStatus> prototypepaincompats$hashMap = new HashMap<>();

    @ModifyVariable(method = "getVisibleMoodles", remap = false, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/adinvas/prototype_pain/client/moodles/AbstractMoodleVisual;clone()Lnet/adinvas/prototype_pain/client/moodles/AbstractMoodleVisual;"), index = 4)
    private static AbstractMoodleVisual getVisibleMoodles(AbstractMoodleVisual value, Player player) {
        if (value instanceof TickableMoodle) {
            ((TickableMoodle) value).tick(player);
        } // else {
//            String moodleName = value.getClass().getSimpleName();
//            MoodleStatus newStatus = value.calculateStatus(player);
//
//            if (!prototypepaincompats$hashMap.containsKey(moodleName)) {
//                prototypepaincompats$hashMap.put(moodleName, MoodleStatus.NONE);
//            }
//
//            if (newStatus.ordinal() > prototypepaincompats$hashMap.get(moodleName).ordinal()) {
//                ThoughtMain.sendMoodleThought(player, moodleName, newStatus, prototypepaincompats$hashMap.get(moodleName));
//            }
//
//            prototypepaincompats$hashMap.replace(moodleName, newStatus);
//        }

        return value;
    }
}
