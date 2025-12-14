package net.kagaries.prototypepaincompats.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.io.*;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SyncCustomHealthPacket {
    final CompoundTag tag;
    final UUID target;

    public SyncCustomHealthPacket(CompoundTag tag, UUID target) {
        this.tag = tag;
        this.target = target;
    }

    public SyncCustomHealthPacket(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
        this.target = buf.readUUID();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
        buf.writeUUID(this.target);
    }

    public static void handle(SyncCustomHealthPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = (NetworkEvent.Context)ctx.get();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ModClientPacketHandlers.handleSyncHealth(msg));
        c.setPacketHandled(true);
    }

    public static CompoundTag decompressNBT(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);

        CompoundTag var4;
        try (
                GZIPInputStream gzip = new GZIPInputStream(bais);
                DataInputStream dis = new DataInputStream(gzip);
        ) {
            var4 = NbtIo.read(dis);
        }

        return var4;
    }

    public static byte[] compressNBT(CompoundTag tag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (
                GZIPOutputStream gzip = new GZIPOutputStream(baos);
                DataOutputStream dos = new DataOutputStream(gzip);
        ) {
            NbtIo.write(tag, dos);
        }

        return baos.toByteArray();
    }
}
