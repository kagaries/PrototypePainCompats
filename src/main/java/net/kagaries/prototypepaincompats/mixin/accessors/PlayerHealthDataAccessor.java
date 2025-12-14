package net.kagaries.prototypepaincompats.mixin.accessors;

import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.LimbStatistics;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PlayerHealthData.class)
public interface PlayerHealthDataAccessor {
    @Accessor("limbStats")
    Map<Limb, LimbStatistics> getLimbStats();
}
