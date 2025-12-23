package net.kagaries.prototypepaincompats.mixin;

import net.adinvas.prototype_pain.client.gui.HealthScreen;
import net.kagaries.prototypepaincompats.network.GuiSyncCustomTogglePacket;
import net.kagaries.prototypepaincompats.network.ModNetwork;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HealthScreen.class)
public abstract class HealthScreenMixin {

    @Shadow
    private Player target;

    @Inject(method = "m_7856_", at = @At("TAIL"), remap = false)
    private void init(CallbackInfo ci) {
        ModNetwork.CHANNEL.sendToServer(new GuiSyncCustomTogglePacket(true, this.target.getUUID()));
    }

    @Inject(method = "m_7379_", at = @At("TAIL"), remap = false)
    private void onCloseMixin(CallbackInfo ci) {
        ModNetwork.CHANNEL.sendToServer(new GuiSyncCustomTogglePacket(false, this.target.getUUID()));
    }
}
