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
public class HealthScreenMixin {

    @Shadow
    private Player target;

    @Inject(method = "init", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/network/simple/SimpleChannel;sendToServer(Ljava/lang/Object;)V"))
    private void init(CallbackInfo ci) {
        ModNetwork.CHANNEL.sendToServer(new GuiSyncCustomTogglePacket(true, this.target.getUUID()));
    }

    @Inject(method = "onClose", remap = false, at = @At("TAIL"))
    private void onClose(CallbackInfo ci) {
        ModNetwork.CHANNEL.sendToServer(new GuiSyncCustomTogglePacket(false, this.target.getUUID()));
    }
}
