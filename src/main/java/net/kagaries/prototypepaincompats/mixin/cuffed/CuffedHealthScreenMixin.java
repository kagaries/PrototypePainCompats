package net.kagaries.prototypepaincompats.mixin.cuffed;

import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import net.adinvas.prototype_pain.client.gui.HealthScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HealthScreen.class)
public abstract class CuffedHealthScreenMixin {

    @Shadow
    public abstract void m_7379_();

    @Inject(method = "m_7856_", remap = false, at = @At(value = "TAIL"))
    private void init(CallbackInfo ci) {
        if (Minecraft.getInstance().player != null) {
            IRestrainableEntity restrainableEntity = (IRestrainableEntity) Minecraft.getInstance().player;

            if (restrainableEntity.isRestrained()) {
                this.m_7379_();
            }
        }
    }
}
