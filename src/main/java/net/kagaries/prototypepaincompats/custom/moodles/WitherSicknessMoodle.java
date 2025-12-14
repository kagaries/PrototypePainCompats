package net.kagaries.prototypepaincompats.custom.moodles;

import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.client.moodles.AbstractMoodleVisual;
import net.adinvas.prototype_pain.client.moodles.MoodleStatus;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.kagaries.prototypepaincompats.custom.CustomHealthProvider;
import net.kagaries.prototypepaincompats.custom.CustomPlayerHealthData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class WitherSicknessMoodle extends AbstractMoodleVisual {
    @Override
    public MoodleStatus calculateStatus(Player player) {
        Optional<Double> wither_sickness = player.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA).map(CustomPlayerHealthData::getMaxWitherSickness);
        if ((Double)wither_sickness.orElse((double)0.0F) > (double)80.0F) {
            return MoodleStatus.CRITICAL;
        } else if ((Double)wither_sickness.orElse((double)0.0F) > (double)60.0F) {
            return MoodleStatus.HEAVY;
        } else if ((Double)wither_sickness.orElse((double)0.0F) > (double)40.0F) {
            return MoodleStatus.NORMAL;
        } else {
            return (Double)wither_sickness.orElse((double)0.0F) > (double)25.0F ? MoodleStatus.LIGHT : MoodleStatus.NONE;
        }
    }

    @Override
    public ResourceLocation renderIcon(GuiGraphics ms, float partialTicks, int x, int y) {
        ResourceLocation tex = ResourceLocation.fromNamespaceAndPath("prototype_pain", "textures/gui/moodles/infection_moodle.png");
        ms.blit(tex, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
        return tex;
    }

    public List<Component> getTooltip(Player player) {
        List<Component> componentList = new ArrayList();
        switch (this.getMoodleStatus()) {
            case LIGHT:
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.wither_sickness.title1"));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.description1").withStyle(ChatFormatting.GRAY));
                break;
            case NORMAL:
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.wither_sickness.title2").withStyle(ChatFormatting.YELLOW));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.description2").withStyle(ChatFormatting.GRAY));
                break;
            case HEAVY:
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.wither_sickness.title3").withStyle(ChatFormatting.GOLD));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.description3").withStyle(ChatFormatting.GRAY));
                break;
            case CRITICAL:
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.wither_sickness.title4").withStyle(ChatFormatting.RED));
                componentList.add(Component.translatable("prototype_pain.gui.moodle.infection.description4").withStyle(ChatFormatting.GRAY));
        }

        return componentList;
    }
}
