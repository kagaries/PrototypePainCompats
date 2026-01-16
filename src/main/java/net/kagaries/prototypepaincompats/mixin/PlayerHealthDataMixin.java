package net.kagaries.prototypepaincompats.mixin;

import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.kagaries.prototypepaincompats.custom.CustomHealthProvider;
import net.kagaries.prototypepaincompats.custom.CustomPlayerHealthData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerHealthData.class)
public class PlayerHealthDataMixin {
    @Unique
    private ServerPlayer prototypePainCompats$player;

    @Inject(method = "tickUpdate", at = @At("HEAD"), remap = false)
    private void cachePlayer(ServerPlayer player, CallbackInfo ci) {
        this.prototypePainCompats$player = player;
    }

    @ModifyVariable(method = "calculateBPM", remap = false, at = @At(value = "INVOKE", target = "Lnet/adinvas/prototype_pain/limbs/PlayerHealthData;getNetOpiodids()F"), ordinal = 0)
    private int modifyBPMBeforeSet(int newBPM) {
        ServerPlayer player = this.prototypePainCompats$player;
        if (player == null) return newBPM;

        Optional<CustomPlayerHealthData> data = player.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).resolve();

        if (data.isPresent() && data.get().panic > 0.0F) {
            newBPM += (int)(data.get().panic);
        }

        return newBPM;
    }
}
