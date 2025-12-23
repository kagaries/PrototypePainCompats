package net.kagaries.prototypepaincompats.events;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.init.ModDamageTypes;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.kagaries.prototypepaincompats.custom.CustomHealthProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CuffedEvents {
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            Level level = player.level();
            RegistryAccess registryAccess = level.registryAccess();
            DamageType hangingDamageType = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).get(ModDamageTypes.HANG).orElseThrow().get();

            if (event.getSource().type() == hangingDamageType) {
                player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                    h.setOxygen(h.getOxygen() - 10f);
                    if (player.getDeltaMovement().y > -2) {
                        h.setLimbFracture(Limb.HEAD, 100f);
                    }
                });
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.side.isClient()) return;
        if (event.phase == TickEvent.Phase.END) {
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(playerHealthData -> {
                player.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent(customPlayerHealthData -> {
                    if (player instanceof IAnchorableEntity iAnchorableEntity) {
                        if (iAnchorableEntity.isAnchored()) {
                            double minDist = (double)(Float) CuffedMod.SERVER_CONFIG.ANCHORING_MAX_CHAIN_LENGTH.get();
                            double maxDist = (double)player.fallDistance > 0.2F ? (Float)CuffedMod.SERVER_CONFIG.ANCHORING_MAX_CHAIN_LENGTH.get() : (Float)CuffedMod.SERVER_CONFIG.ANCHORING_SUFFOCATION_LENGTH.get();
                            if ((double)player.distanceTo(iAnchorableEntity.getAnchor()) > minDist) {
                                if ((double)player.distanceTo(iAnchorableEntity.getAnchor()) > maxDist) {
                                    playerHealthData.setBreathing(false);
                                }
                            }
                        }
                    }
                });
            });
        }
    }
}
