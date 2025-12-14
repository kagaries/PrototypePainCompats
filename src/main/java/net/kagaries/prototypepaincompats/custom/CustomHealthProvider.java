package net.kagaries.prototypepaincompats.custom;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomHealthProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<CustomPlayerHealthData> CUSTOM_HEALTH_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    private CustomPlayerHealthData data;
    private final LazyOptional<CustomPlayerHealthData> optional = LazyOptional.of(this::create);

    private CustomPlayerHealthData create() {
        if (data == null) data = new CustomPlayerHealthData();
        return data;
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CUSTOM_HEALTH_DATA ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        create().serializeNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        create().deserializeNBT(nbt);
    }
}
