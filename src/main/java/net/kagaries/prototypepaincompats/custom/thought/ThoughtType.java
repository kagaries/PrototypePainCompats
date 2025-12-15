package net.kagaries.prototypepaincompats.custom.thought;

import net.minecraft.ChatFormatting;

public enum ThoughtType {
    Good(ChatFormatting.GREEN),
    Normal(ChatFormatting.WHITE),
    Uncertain(ChatFormatting.YELLOW),
    Bad(ChatFormatting.RED),
    Horrible(ChatFormatting.DARK_RED);

    private final ChatFormatting chatFormatting;

    ThoughtType(ChatFormatting chatFormatting) {
        this.chatFormatting = chatFormatting;
    }

    public ChatFormatting getChatFormatting() {
        return chatFormatting;
    }
}
