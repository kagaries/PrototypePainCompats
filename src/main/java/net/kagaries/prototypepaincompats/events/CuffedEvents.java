package net.kagaries.prototypepaincompats.events;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.init.ModDamageTypes;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
}
