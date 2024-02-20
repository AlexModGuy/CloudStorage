package com.github.alexthe668.cloudstorage.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class CloudMiniButton extends Button {
    private final int index;
    private final Screen parent;

    public CloudMiniButton(Screen parent, int x, int y, int index, Button.OnPress onPress) {
        super(x, y, 14, 14, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.index = index;
        this.parent = parent;
        this.setTooltip(Tooltip.create(Component.translatable("cloudstorage.container.cloud_chest.button_" + index)));
    }

    public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTick) {
        int i = 214;
        int j = 15 + 14 * index;
        if(this.active){
            i += 14;
            if (this.isHoveredOrFocused()) {
                i += 14;
            }
        }
        guiGraphics.blit(CloudChestScreen.TEXTURE, this.getX(), this.getY(), i, j, 14, 14);
    }
}