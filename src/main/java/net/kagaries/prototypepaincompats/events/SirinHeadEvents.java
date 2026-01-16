package net.kagaries.prototypepaincompats.events;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.meme.sirenhead.init.SirenHeadModEntities;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class SirinHeadEvents {
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getSource().getEntity() != null) {
                if (event.getSource().getEntity().getType().equals(SirenHeadModEntities.SIREN_HEAD_CRAWL_HAD_TO_MAKE_SEPRATE_ENTITY_FOR_THIS_VER.get())) {
                    if (!event.getEntity().level().isClientSide) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                            Limb limb = Limb.weigtedRandomLimb();


                            if (limb != Limb.HEAD && limb != Limb.CHEST) {
                                h.handleAmputation(limb, event.getAmount() + 10.5f, 0f, player);
                            }

                            if (limb == Limb.HEAD) {
                                int random = RandomSource.create().nextIntBetweenInclusive(0, 100);

                                if (random >= 90) {
                                    if (!h.isLeftEyeBlind()) {
                                        h.setLeftEyeBlind(true);
                                    } else {
                                        h.setRightEyeBlind(true);
                                    }
                                }
                            }

                            h.handleRandomDamage(event.getAmount() + 6f, player);
                        });
                    }
                } else if (event.getSource().getEntity().getType().equals(SirenHeadModEntities.SIREN_HEAD.get())) {
                    if (!event.getEntity().level().isClientSide) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h-> h.handleRandomDamage(event.getAmount() + 5f, player));
                    }
                }
            }
        }
    }
}
