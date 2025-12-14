package net.kagaries.prototypepaincompats.network;

import net.adinvas.prototype_pain.network.GuiSyncTogglePacket;
import net.adinvas.prototype_pain.network.SyncTracker;
import net.kagaries.prototypepaincompats.custom.CustomSyncTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GuiSyncCustomTogglePacket {
    private final boolean enabled;
    private final UUID targetId;

    public GuiSyncCustomTogglePacket(boolean enable, UUID targetId) {
        this.enabled = enable;
        this.targetId = targetId;
    }

    public GuiSyncCustomTogglePacket(FriendlyByteBuf buf) {
        this.enabled = buf.readBoolean();
        this.targetId = buf.readUUID();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.enabled);
        buf.writeUUID(this.targetId);
    }

    public static void handle(GuiSyncCustomTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                if (msg.enabled) {
                    CustomSyncTracker.add(player.getUUID(), msg.targetId);
                } else {
                    CustomSyncTracker.remove(player);
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
