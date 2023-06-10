package com.github.alexthe668.cloudstorage.client.gui;

import com.github.alexthe666.citadel.client.gui.GuiBasicBook;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GuideBookScreen extends GuiBasicBook {

    private static final ResourceLocation ROOT = new ResourceLocation("cloudstorage:book/guide_book/root.json");

    public GuideBookScreen(ItemStack bookStack) {
        super(bookStack, Component.translatable("item.cloudstorage.guide_book"));
    }

    protected int getBindingColor() {
        return 0XDE2727;
    }

    public ResourceLocation getRootPage() {
        return ROOT;
    }

    public String getTextFileDirectory() {
        return "cloudstorage:book/guide_book/";
    }
}

