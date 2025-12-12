package net.kagaries.prototypepaincompats.mixin.sirin;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.meme.sirenhead.entity.SirenHeadEntity;
import net.meme.sirenhead.procedures.SonicboomWhileProjectileFlyingTickProcedure;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;

@Mixin(SonicboomWhileProjectileFlyingTickProcedure.class)
public class SirenSonicboomTickMixin {
    @Inject(method = "execute", cancellable = true, remap = false, at = @At(value = "HEAD"))
    private static void execute(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        Vec3 _center = new Vec3(x, y, z);
        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, (new AABB(_center, _center)).inflate(8.0), (e) -> {
            return true;
        }).stream().sorted(Comparator.comparingDouble((_entcnd) -> {
            return _entcnd.distanceToSqr(_center);
        })).toList();

        for (Entity entityiterator : _entfound) {
            if (entityiterator instanceof LivingEntity livingEntity && !(entityiterator instanceof SirenHeadEntity)) {
                if (livingEntity instanceof Player player) {
                    if (!player.level().isClientSide) {
                        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
                            h.setBrainHealth(h.getBrainHealth() - 10.5f);
                            h.setHearingLoss(h.getHearingLoss() + 0.25f);
                        });

                        player.hurt(player.level().damageSources().generic(), 0);
                    }
                }
            }
        }

        ci.cancel();
    }
}
