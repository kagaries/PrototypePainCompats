package net.kagaries.prototypepaincompats.network;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.kagaries.prototypepaincompats.custom.CustomHealthProvider;
import net.kagaries.prototypepaincompats.custom.CustomPlayerHealthData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class ModClientPacketHandlers {
    public static void handleSyncHealth(SyncCustomHealthPacket msg) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer viewer = mc.player;
        if (viewer != null && mc.level != null) {
            Player target = mc.level.getPlayerByUUID(msg.target);
            if (target != null) {
                if (target == viewer) {
                    viewer.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((cap) -> cap.deserializeNBT(msg.tag));
                } else {
                    target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((cap) -> cap.deserializeNBT(msg.tag));
                }

            }
        }
    }
}
