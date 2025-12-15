package net.kagaries.prototypepaincompats.custom.moodles;

import net.adinvas.prototype_pain.client.moodles.AbstractMoodleVisual;
import net.adinvas.prototype_pain.client.moodles.MoodleStatus;
import net.kagaries.prototypepaincompats.custom.CustomHealthProvider;
import net.kagaries.prototypepaincompats.custom.thought.ThoughtMain;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PanicMoodle extends AbstractMoodleVisual implements TickableMoodle {
    private MoodleStatus lastMoodleStatus = MoodleStatus.NONE;

    @Override
    public MoodleStatus calculateStatus(Player player) {
        float panic = player.getCapability(CustomHealthProvider.CUSTOM_HEALTH_DATA)
                .map(data -> data.panic)
                .orElse(0.0F);

        if (panic > 90.0F) return MoodleStatus.CRITICAL;
        if (panic > 60.0F) return MoodleStatus.HEAVY;
        if (panic > 30.0F) return MoodleStatus.NORMAL;
        if (panic > 0.15F) return MoodleStatus.LIGHT;
        return MoodleStatus.NONE;
    }

    public void tick(Player player) {
        MoodleStatus newStatus = calculateStatus(player);

        if (newStatus.ordinal() > lastMoodleStatus.ordinal()) {
            ThoughtMain.sendMoodleThought(player, "panic", newStatus, lastMoodleStatus);
        }

        lastMoodleStatus = newStatus;
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
            case HEAVY:
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.title3").withStyle(ChatFormatting.YELLOW));
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.description3").withStyle(ChatFormatting.GRAY));
                break;
            case CRITICAL:
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.title4").withStyle(ChatFormatting.RED));
                componentList.add(Component.translatable("prototype_pain_compats.gui.moodle.panic.description4").withStyle(ChatFormatting.GRAY));
        }

        return componentList;
    }
}
