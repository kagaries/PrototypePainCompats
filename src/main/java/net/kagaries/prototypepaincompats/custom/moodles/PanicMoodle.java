package net.kagaries.prototypepaincompats.custom.moodles;

import net.adinvas.prototype_pain.client.moodles.AbstractMoodleVisual;
import net.adinvas.prototype_pain.client.moodles.MoodleStatus;
import net.kagaries.prototypepaincompats.custom.CustomHealthProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PanicMoodle extends AbstractMoodleVisual {
    @Override
    public MoodleStatus calculateStatus(Player player) {
        AtomicReference<MoodleStatus> status = new AtomicReference<>(this.getMoodleStatus());
        player.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).ifPresent(customPlayerHealthData -> {
            float panic = customPlayerHealthData.panic;
            if (panic > 90.0F) {
                status.set(MoodleStatus.CRITICAL);
            } else if (panic > 50.0F) {
                status.set(MoodleStatus.NORMAL);
            } else if (panic > 10.0F) {
                status.set(MoodleStatus.LIGHT);
            } else {
                status.set(MoodleStatus.NONE);
            }
        });

        return status.get() != null ? status.get() : this.getMoodleStatus();
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = ResourceLocation.fromNamespaceAndPath("prototype_pain", "textures/gui/moodles/blood_moodle.png");
        ms.blit(tex, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
        return tex;
    }

    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList();
        switch (this.getMoodleStatus()) {
            case LIGHT:
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.title1"));
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.description1").withStyle(ChatFormatting.GRAY));
                break;
            case NORMAL:
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.title2").withStyle(ChatFormatting.YELLOW));
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.description2").withStyle(ChatFormatting.GRAY));
                break;
            case CRITICAL:
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.title3").withStyle(ChatFormatting.RED));
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.description3").withStyle(ChatFormatting.GRAY));
        }

        return componentList;
    }
}
