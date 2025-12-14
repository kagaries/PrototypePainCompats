package net.kagaries.prototypepaincompats.mixin.accessors;

import net.adinvas.prototype_pain.limbs.LimbStatistics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LimbStatistics.class)
public interface LimbStatisticsAccessor {
    @Accessor("amputated")
    boolean getAmputated();
}
