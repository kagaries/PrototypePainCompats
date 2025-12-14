package net.kagaries.prototypepaincompats.custom;

import com.mojang.logging.LogUtils;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.LimbStatistics;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.kagaries.prototypepaincompats.mixin.accessors.LimbStatisticsAccessor;
import net.kagaries.prototypepaincompats.mixin.accessors.PlayerHealthDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.EnumMap;
import java.util.Map;

public class CustomPlayerHealthData {
    private final EnumMap<Limb, CustomLimbStatistics> limbStats = new EnumMap<>(Limb.class);

    public CustomPlayerHealthData() {
        for (Limb limb : Limb.values()) {
            limbStats.put(limb, new CustomLimbStatistics());
        }
    }

    public boolean isReducedDirty = false;

    //SANITY
    public float mood = 100.0F; //Custom internal sanity value to be applied across all mods that could benefit from it
    public float panic = 0.0F;

    //MOD SPECIFIC
    //MEKANISM
    public float radiationSickness = 0.0F;

    //ZOMBIE
    public float zombieInfection = 0.0F;
    public float craving = 0.0F;
    public boolean brainDecay = false;

    public CustomLimbStatistics getLimbStats(Limb limb) {
        return limbStats.get(limb);
    }

    public String baseToString() {
        return "CustomPlayerHealthData{panic=" + this.panic + "}";
    }

    public void setPanic(float value) {
        this.panic = value;
    }

    public void setWitherSickness(Limb limb, float value) {
        this.ensureLimb(limb).wither_sickness = value;
    }

    public void setClean() {
        this.isReducedDirty = false;
    }

    public void tickUpdate(ServerPlayer player) {
        if (player.isAlive()) {
            for(Limb limb : this.limbStats.keySet()) {
                this.updateLimb(player, limb);
            }
        }
    }

    public void updateLimb(ServerPlayer player, Limb limb) {
        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(playerHealthData ->
                player.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent(customPlayerHealthData -> {
            LimbStatistics stats = ((PlayerHealthDataAccessor) playerHealthData).getLimbStats().get(limb);
            CustomLimbStatistics customLimbStatistics = customPlayerHealthData.limbStats.get(limb);

            if (((LimbStatisticsAccessor) stats).getAmputated()) {
                customLimbStatistics.wither_sickness = 0.0F;
                customLimbStatistics.nerveDamage = 0.0F;
            } else {
                this.calculateWitherSicknessForLimb(limb, playerHealthData);

                if (customLimbStatistics.wither_sickness >= 50.0F) {
                    playerHealthData.applySkinDamage(limb, playerHealthData.getINFECTION_MUSCLE_DRAIN() * 1.5f);
                    if (customLimbStatistics.wither_sickness > 75.0F) {
                        playerHealthData.applyMuscleDamage(limb, playerHealthData.getINFECTION_MUSCLE_DRAIN() * 1.5f, player);
                    }
                } else if (customLimbStatistics.wither_sickness > 0F) {
                    playerHealthData.setLimbSkinHeal(limb, false);
                    playerHealthData.setLimbMuscleHeal(limb, false);
                }

                this.witherSicknessSpread(limb);
            }
        }));
    }

    public CompoundTag serializeNBT(CompoundTag nbt) {
        nbt.putFloat("Mood", mood);
        nbt.putFloat("Panic", panic);
        nbt.putFloat("RadiationSickness", radiationSickness);
        nbt.putFloat("ZombieInfection", zombieInfection);
        nbt.putFloat("Craving", craving);
        nbt.putBoolean("BrainDecay", brainDecay);

        ListTag limbList = new ListTag();

        for(Map.Entry<Limb, CustomLimbStatistics> entry : this.limbStats.entrySet()) {
            CompoundTag limbTag = new CompoundTag();
            limbTag.putString("LimbName", ((Limb) entry.getKey()).name());
            CustomLimbStatistics limbStatistics = (CustomLimbStatistics) entry.getValue();
            limbTag.putFloat("NerveDamage", limbStatistics.nerveDamage);
            limbTag.putFloat("WitherSickness", limbStatistics.wither_sickness);
            limbList.add(limbTag);
        }

        nbt.put("LimbStats", limbList);
        return nbt;
    }

    public CompoundTag serilizeReducedNbt(CompoundTag tag) {
        ListTag limbList = new ListTag();

        for(Map.Entry<Limb, CustomLimbStatistics> entry : this.limbStats.entrySet()) {
            CompoundTag limbTag = new CompoundTag();
            limbTag.putString("LimbName", entry.getKey().name());
            CustomLimbStatistics stats = entry.getValue();
            limbList.add(limbTag);
        }

        tag.put("LimbStats", limbList);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("Mood")) {
            this.mood = tag.getFloat("Mood");
        }

        if (tag.contains("Panic")) {
            this.panic = tag.getFloat("Panic");
        }

        if (tag.contains("RadiationSickness")) {
            this.radiationSickness = tag.getFloat("RadiationSickness");
        }

        if (tag.contains("ZombieInfection")) {
            this.zombieInfection = tag.getFloat("ZombieInfection");
        }

        if (tag.contains("Craving")) {
            this.craving = tag.getFloat("Craving");
        }

        if (tag.contains("BrainDecay")) {
            this.brainDecay = tag.getBoolean("BrainDecay");
        }

        ListTag limbList = tag.getList("LimbStats", 10);

        for (int i = 0; i < limbList.size(); ++i) {
            CompoundTag limbTag = limbList.getCompound(i);
            Limb limb = Limb.valueOf(limbTag.getString("LimbName"));
            CustomLimbStatistics limbStatistics = (CustomLimbStatistics) this.limbStats.computeIfAbsent(limb, (k) -> new CustomLimbStatistics());
            if (limbTag.contains("NerveDamage")) {
                limbStatistics.nerveDamage = limbTag.getFloat("NerveDamage");
            }

            if (limbTag.contains("WitherSickness")) {
                limbStatistics.wither_sickness = limbTag.getFloat("WitherSickness");
            }
        }
    }

    public void resetToDefaults(Player player) {
        this.limbStats.clear();

        for (Limb limb : Limb.values()) {
            this.limbStats.put(limb, new CustomLimbStatistics());
        }

        this.mood = 100.0F;
        this.panic = 0.0F;
        this.radiationSickness = 0.0F;
        this.zombieInfection = 0.0F;
        this.craving = 0.0F;
        this.brainDecay = false;

        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(playerHealthData -> {
            playerHealthData.recalcTotalPain();
            playerHealthData.recalculateConsciousness();
        });
    }

    public void witherSicknessSpread(Limb limb) {
        if (this.limbStats.get(limb).wither_sickness > 75.0F) {
            float chance = this.limbStats.get(limb).wither_sickness - 75.0F;
            if (Math.random() > (double)chance) {
                Limb conectedLimb = limb.randomFromConectedLimb();
                if (this.limbStats.get(conectedLimb).wither_sickness <= 0.0F) {
                    ++this.limbStats.get(conectedLimb).wither_sickness;
                }
            }
        }
    }

    public void calculateWitherSicknessForLimb(Limb limb, PlayerHealthData playerHealthData) {
        float wither_sickness_progress = (float)((double)playerHealthData.getImmunity() * (playerHealthData.getIMMUNITY_STRENGTH() * 0.5) * (double)-0.001188F + (double)0.18F);

        float wither_sickness = this.getLimbStats(limb).wither_sickness;
        if (wither_sickness > 0.0F) {
            wither_sickness += wither_sickness_progress / 15.0F;
            wither_sickness = Mth.clamp(wither_sickness, 0.0F, 100.0F);
            this.limbStats.get(limb).wither_sickness = wither_sickness;
            LogUtils.getLogger().info("WitherSickness: " + limb + " : " + wither_sickness);
            this.witherSicknessSpread(limb);
        }

        this.limbStats.get(limb).wither_sickness = Mth.clamp(this.limbStats.get(limb).wither_sickness, 0.0F, 100.0F);
    }

    public double getMaxWitherSickness() {
        double wither_sickness = this.limbStats.values().stream().mapToDouble((ls) -> (double)ls.wither_sickness).max().orElse((double)0.0F);
        return Math.max(wither_sickness, (double)0.0F);
    }

    private CustomLimbStatistics ensureLimb(Limb limb) {
        CustomLimbStatistics stats = this.limbStats.get(limb);
        if (stats == null) {
            stats = new CustomLimbStatistics();
            this.limbStats.put(limb, stats);
            PrototypePain.LOGGER.warn("PlayerHealthData: missing LimbStatistics for {} — created default", limb);
        }

        return stats;
    }
}
