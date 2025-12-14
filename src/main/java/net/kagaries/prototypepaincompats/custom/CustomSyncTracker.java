package net.kagaries.prototypepaincompats.custom;

import net.kagaries.prototypepaincompats.network.ModNetwork;
import net.kagaries.prototypepaincompats.network.SyncCustomHealthPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomSyncTracker {
    private static final Map<UUID, UUID> syncing = new HashMap();
    static int tickCounter = 0;
    static int tickCounterReduced = 0;

    public static void add(UUID viewer, UUID targetId) {
        syncing.put(viewer, targetId);
    }

    public static void remove(ServerPlayer player) {
        syncing.remove(player.getUUID());
    }

    public static void tick(MinecraftServer server) {
        for(ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            UUID targetId = (UUID)syncing.get(viewer.getUUID());
            if (targetId != null) {
                ServerPlayer target = server.getPlayerList().getPlayer(targetId);
                if (target != null) {
                    target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((cap) -> {
                        CompoundTag tag = cap.serializeNBT(new CompoundTag());
                        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), new SyncCustomHealthPacket(tag, target.getUUID()));
                    });
                }
            }
        }

    }

    public static void tickEveryone(MinecraftServer server) {
        ++tickCounter;
        if (tickCounter >= 3) {
            tickCounter = 0;

            for(ServerPlayer viewer : server.getPlayerList().getPlayers()) {
                UUID targetId = (UUID)syncing.get(viewer.getUUID());
                if (targetId == viewer.getUUID()) {
                    return;
                }

                targetId = viewer.getUUID();
                if (targetId != null) {
                    ServerPlayer target = server.getPlayerList().getPlayer(targetId);
                    if (target != null) {
                        target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((cap) -> {
                            CompoundTag tag = cap.serializeNBT(new CompoundTag());
                            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), new SyncCustomHealthPacket(tag, target.getUUID()));
                        });
                    }
                }
            }

        }
    }

    public static void tickEveryoneReducedBroadcast(MinecraftServer server) {
        ++tickCounterReduced;
        if (tickCounterReduced >= 20) {
            tickCounterReduced = 0;

            for(ServerPlayer viewer : server.getPlayerList().getPlayers()) {
                for(ServerPlayer target : server.getPlayerList().getPlayers()) {
                    boolean isDirty = (Boolean)target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).map((h) -> h.isReducedDirty).orElse(false);
                    if (isDirty) {
                        target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((cap) -> {
                            CompoundTag tag = cap.serilizeReducedNbt(new CompoundTag());
                            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), new SyncCustomHealthPacket(tag, target.getUUID()));
                        });
                    }
                }
            }

        }
    }

    public static void onJoin(ServerPlayer viewer, MinecraftServer server) {
        for(ServerPlayer target : server.getPlayerList().getPlayers()) {
            target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((cap) -> {
                CompoundTag tag = cap.serilizeReducedNbt(new CompoundTag());
                ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), new SyncCustomHealthPacket(tag, target.getUUID()));
            });
        }

        viewer.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((cap) -> {
            CompoundTag tag = cap.serilizeReducedNbt(new CompoundTag());

            for(ServerPlayer target : server.getPlayerList().getPlayers()) {
                if (target != viewer) {
                    ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> target), new SyncCustomHealthPacket(tag, viewer.getUUID()));
                }
            }

        });
    }
}
