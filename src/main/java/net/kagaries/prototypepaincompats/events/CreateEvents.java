package net.kagaries.prototypepaincompats.events;

import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.kagaries.prototypepaincompats.custom.CustomPlayerHealthData;

//TODO: Add compatibility for Create 5
public class CreateEvents {
    @SubscribeEvent
    public static void hurtEvent(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getSource().type() == CreateDamageSources.crush(player.level()).type()) {
                if (!player.level().isClientSide) {
                    player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                        Limb limb = CustomPlayerHealthData.getRandomNoneAmputatedLimb(player);

                        if (!(h.getLimbFracture(limb) > 10.0f)) {
                            h.setLimbFracture(limb, 100f);
                        }

                        h.handleBluntDamage(event.getAmount(), player, limb);
                    });
                }
            }
        }
    }
}
