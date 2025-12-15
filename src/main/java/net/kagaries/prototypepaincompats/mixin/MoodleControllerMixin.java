package net.kagaries.prototypepaincompats.mixin;

import net.adinvas.prototype_pain.client.moodles.AbstractMoodleVisual;
import net.adinvas.prototype_pain.client.moodles.MoodleController;
import net.kagaries.prototypepaincompats.custom.moodles.TickableMoodle;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MoodleController.class)
public class MoodleControllerMixin {
    @ModifyVariable(method = "getVisibleMoodles", remap = false, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/adinvas/prototype_pain/client/moodles/AbstractMoodleVisual;clone()Lnet/adinvas/prototype_pain/client/moodles/AbstractMoodleVisual;"), index = 4)
    private static AbstractMoodleVisual getVisibleMoodles(AbstractMoodleVisual value, Player player) {
        if (value instanceof TickableMoodle) {
            ((TickableMoodle) value).tick(player);
        }

        return value;
    }
}
