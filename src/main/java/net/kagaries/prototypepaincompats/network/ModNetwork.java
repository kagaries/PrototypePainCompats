package net.kagaries.prototypepaincompats.network;

import net.kagaries.prototypepaincompats.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(Main.MODID, "prototype_pain_compat_main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register() {
        CHANNEL.registerMessage(0, SyncCustomHealthPacket.class, SyncCustomHealthPacket::write, SyncCustomHealthPacket::new, SyncCustomHealthPacket::handle);
        CHANNEL.registerMessage(1, GuiSyncCustomTogglePacket.class, GuiSyncCustomTogglePacket::write, GuiSyncCustomTogglePacket::new, GuiSyncCustomTogglePacket::handle);
    }
}
