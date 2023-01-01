package com.github.alexthe668.cloudstorage.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;


@OnlyIn(Dist.CLIENT)
public class CloudMiniButton extends Button {
    private final int index;
    private final Screen parent;

    public CloudMiniButton(Screen parent, int x, int y, int index, Button.OnPress onPress) {
        super(x, y, 14, 14, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.index = index;
        this.parent = parent;
    }


    public void renderToolTip(PoseStack poseStack, int x, int y) {
        List<Component> list = List.of( Component.translatable("cloudstorage.container.cloud_chest.button_" + index));
        parent.renderTooltip(poseStack, list, Optional.empty(), x, y);

    }

    public void renderButton(PoseStack poseStack, int x, int y, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CloudChestScreen.TEXTURE);
        int i = 214;
        int j = 15 + 14 * index;
        if(this.active){
            i += 14;
            if (this.isHoveredOrFocused()) {
                i += 14;
            }
        }
        this.blit(poseStack, this.getX(), this.getY(), i, j, 14, 14);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, x, y);
        }

    }
}