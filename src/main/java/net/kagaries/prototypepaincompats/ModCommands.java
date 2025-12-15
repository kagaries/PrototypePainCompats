package net.kagaries.prototypepaincompats;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.limbs.Limb;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.kagaries.prototypepaincompats.custom.CustomHealthProvider;
import net.kagaries.prototypepaincompats.custom.CustomPlayerHealthData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Optional;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ModCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
//        dispatcher.register((Commands.literal(Main.MODID).requires(source -> source.hasPermission(0)).then((Commands.literal("checkBody").requires((source) -> source.hasPermission(2))).then((Commands.argument("target", EntityArgument.player()).executes(ctx -> {
//            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
//            Optional<String> text = target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).map(CustomPlayerHealthData::baseToString);
//            ctx.getSource().sendSuccess(() -> Component.literal(String.valueOf(text)), false);
//            return 1;
//        }))))).then(Commands.literal("setBody").requires((source) -> source.hasPermission(2))).then(Commands.argument("target", EntityArgument.player()).then(Commands.argument("field", StringArgumentType.word()).suggests((ctx, builder) -> {
//            builder.suggest("panic");
//            return builder.buildFuture();
//        }).then(Commands.argument("value", FloatArgumentType.floatArg()).executes((ctx) -> {
//            String field = StringArgumentType.getString(ctx, "field").toLowerCase();
//            float value = FloatArgumentType.getFloat(ctx, "value");
//            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
//            target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((h) -> {
//                switch (field) {
//                    case "panic":
//                        h.setPanic(value);
//                    default:
//                        ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("Unknown field: " + field));
//                }
//            });
//            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Applied value " + value + " to " + field + " for " + target.getName().getString()), false);
//            return 1;
//        })))));

        dispatcher.register(Commands.literal(Main.MODID).requires(source -> source.hasPermission(0)).then(Commands.literal("checkBody").requires(source -> source.hasPermission(2)).then(Commands.argument("target", EntityArgument.player()).executes(ctx -> {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
            Optional<String> text = target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).map(CustomPlayerHealthData::baseToString);
            ctx.getSource().sendSuccess(() -> Component.literal(String.valueOf(text)), false);
            return 1;
        }))).then(Commands.literal("setBody").requires(source -> source.hasPermission(2)).then(Commands.argument("target", EntityArgument.player()).then(Commands.argument("field", StringArgumentType.word()).suggests((ctx, builder) -> {
            builder.suggest("panic");
            return builder.buildFuture();
        }).then(Commands.argument("value", FloatArgumentType.floatArg()).executes(ctx -> {
            String field = StringArgumentType.getString(ctx, "field").toLowerCase();
            float value = FloatArgumentType.getFloat(ctx, "value");
            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
            target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((h) -> {
                switch (field) {
                    case "panic":
                        h.setPanic(value);
                        break;
                    default:
                        ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("Unknown field: " + field));
                        break;
                }
            });
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Applied value " + value + " to " + field + " for " + target.getName().getString()), false);
            return 1;
        }))))).then(Commands.literal("setLimb").requires(source -> source.hasPermission(2)).then(Commands.argument("target", EntityArgument.player()).then(Commands.argument("limb", StringArgumentType.word()).suggests((ctx, builder) -> {
            for(Limb e : Limb.values()) {
                builder.suggest(e.name().toLowerCase());
            }

            return builder.buildFuture();
        }).then(Commands.argument("field", StringArgumentType.word()).suggests((ctx, builder) -> {
            builder.suggest("wither_sickness");
            return builder.buildFuture();
        }).then(Commands.argument("value", FloatArgumentType.floatArg()).executes(ctx -> {
            String raw = StringArgumentType.getString(ctx, "limb");
            Limb limb = Limb.valueOf(raw.toUpperCase());
            raw = StringArgumentType.getString(ctx, "field");
            float value = FloatArgumentType.getFloat(ctx, "value");
            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
            String finalRaw = raw;
            target.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent((h) -> {
                switch (finalRaw) {
                    case "wither_sickness" -> h.setWitherSickness(limb, value);
                    default -> ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("Unknown field: " + finalRaw));
                }

            });
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Applied value " + value + " to " + String.valueOf(limb) + " | " + finalRaw + " for " + target.getName().getString()), false);
            return 1;
        })))))).then(Commands.literal("heal").requires(source -> source.hasPermission(2)).then(Commands.argument("targets", EntityArgument.player()).executes(ctx -> {
            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");

            for(ServerPlayer player : targets) {
                player.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent(c -> c.resetToDefaults(player));
            }

            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Healed " + targets.size() + " player(s)."), true);
            return targets.size();
        }))));
    }
}
