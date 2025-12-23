package net.kagaries.prototypepaincompats.events;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.init.ModDamageTypes;
import com.lazrproductions.cuffed.mixin.PlayerMixin;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.kagaries.prototypepaincompats.Main;
import net.kagaries.prototypepaincompats.custom.CustomHealthProvider;
import net.kagaries.prototypepaincompats.custom.CustomPlayerHealthData;
import net.kagaries.prototypepaincompats.custom.CustomSyncTracker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player && !event.getObject().getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).isPresent()) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(Main.MODID, "custom_health"), new CustomHealthProvider());
        }
    }

    @SubscribeEvent
    public static void onRegisterCap(RegisterCapabilitiesEvent event) {
        event.register(CustomPlayerHealthData.class);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.side.isClient()) return;
        if (event.phase == TickEvent.Phase.START) {
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                if (serverPlayer.gameMode.isCreative()) {
                    return;
                }

                ServerLevel level = serverPlayer.serverLevel();
                ProfilerFiller profiler = level.getProfiler();
                profiler.push("prototype_pain_compats:custom_player_health_system");
                event.player.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((customPlayerHealthData) -> {
                    customPlayerHealthData.tickUpdate(serverPlayer);
                });
                profiler.pop();
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ProfilerFiller profiler = server.getProfiler();
        profiler.push("prototype_pain_compats:custom_sync_tracker");
        if (event.phase == TickEvent.Phase.END) {
            CustomSyncTracker.tick(server);
            CustomSyncTracker.tickEveryone(server);
            CustomSyncTracker.tickEveryoneReducedBroadcast(server);
        }

        profiler.pop();
    }

    @SubscribeEvent
    public void onJoin(EntityJoinLevelEvent event) {
        Entity var3 = event.getEntity();
        if (var3 instanceof ServerPlayer player) {
            player.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((h) -> h.isReducedDirty = true);
            CustomSyncTracker.onJoin(player, ServerLifecycleHooks.getCurrentServer());
        }

    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        event.getOriginal().getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent(oldData -> {
            event.getEntity().getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent(newData -> {
                newData.mood = oldData.mood;
            });
        });
    }
}
