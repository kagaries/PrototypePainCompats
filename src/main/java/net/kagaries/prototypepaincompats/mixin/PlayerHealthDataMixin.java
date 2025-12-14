package net.kagaries.prototypepaincompats.mixin;

import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.kagaries.prototypepaincompats.Main;
import net.kagaries.prototypepaincompats.custom.CustomPlayerHealthData;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerHealthData.class)
public class PlayerHealthDataMixin {
    @Inject(method = "serializeNBT", remap = false, at = @At("HEAD"))
    private void serializeNBT(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        nbt.putFloat("Mood", Main.healthData.mood);
        nbt.putFloat("Panic", Main.healthData.panic);
        nbt.putFloat("RadiationSickness", Main.healthData.radiation_sickness);
        nbt.putFloat("ZombieInfection", Main.healthData.zombieInfection);
        nbt.putFloat("Craving", Main.healthData.craving);
        nbt.putBoolean("BrainDecay", Main.healthData.brainDecay);
        nbt.putFloat("WitherSickness", Main.healthData.wither_sickness);
    }
}
