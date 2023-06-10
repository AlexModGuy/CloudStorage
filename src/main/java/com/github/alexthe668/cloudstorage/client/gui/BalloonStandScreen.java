package com.github.alexthe668.cloudstorage.client.gui;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.client.ClientProxy;
import com.github.alexthe668.cloudstorage.inventory.BalloonStandMenu;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.network.MessageRequestCloudInfo;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

public class BalloonStandScreen extends AbstractContainerScreen<BalloonStandMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("cloudstorage:textures/gui/balloon_stand_gui.png");
    private float cloudProgress;
    private float prevCloudProgress;
    private int lastBalloonColor = -1;
    private int tickCount = 0;

    public BalloonStandScreen(BalloonStandMenu menu, Inventory inventory, Component name) {
        super(menu, inventory, name);
        this.imageHeight = 176;
    }

    public void render(GuiGraphics guiGraphics, int x, int y, float partialTick) {
        this.renderBackground(guiGraphics);
        this.renderBg(guiGraphics, partialTick, x, y);
        super.render(guiGraphics, x, y, partialTick);
        this.renderTooltip(guiGraphics, x, y);
    }

    protected void renderBg(GuiGraphics guiGraphics, float f, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        float partialTicks = Minecraft.getInstance().getFrameTime();
        float cloud = (prevCloudProgress + (cloudProgress - prevCloudProgress) * partialTicks) / 10F;
        int cloudTexture = tickCount / 7;
        renderLittleCloud(guiGraphics, partialTicks, cloud * 0.8F, cloudTexture);
    }

    private void renderLittleCloud(GuiGraphics guiGraphics, float partialTick, float alpha, int cloudTexture){
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(TEXTURE, i + 7, j + 15, 176, 66, 66, 66);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void containerTick() {
        prevCloudProgress = cloudProgress;
        tickCount++;
        if (this.menu.getSlot(0).hasItem()) {
            int i = BalloonItem.getBalloonColor(this.menu.getSlot(0).getItem());
            if(i != lastBalloonColor){
                CloudStorage.NETWORK_WRAPPER.sendToServer(new MessageRequestCloudInfo(i));
                lastBalloonColor = i;
            }
            if (cloudProgress < 10F) {
                cloudProgress++;
            }
        } else {
            if (cloudProgress > 0F) {
                cloudProgress--;
            }
        }
    }

    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        float cloud = prevCloudProgress + (cloudProgress - prevCloudProgress) * minecraft.getFrameTime();
        guiGraphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, this.inventoryLabelX,  this.inventoryLabelY + 10, 4210752, false);
        int alpha = (int) ((cloud / 10F) * 255);
        if (alpha > 10) {
            int fade = FastColor.ARGB32.color(alpha, 255, 255, 255);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.8F, 0.8F, 0.8F);
            String color = Integer.toHexString(lastBalloonColor).toUpperCase(Locale.ROOT) + " RGB";
            int textColor = FastColor.ARGB32.multiply(0X85A2B2, fade);
            int textX = 20;
            int textY = 30;
            int r = (int) (lastBalloonColor >> 16 & 255);
            int g = (int) (lastBalloonColor >> 8 & 255);
            int b = (int) (lastBalloonColor & 255);
            guiGraphics.drawString(font, Component.translatable("cloudstorage.container.balloon_stand.color"), textX, textY, textColor, false);
            guiGraphics.drawString(font, color, textX, textY + 10, FastColor.ARGB32.color(alpha, r, g, b), false);
            Component slotsPrivate = Component.translatable("cloudstorage.container.balloon_stand.slots", ClientProxy.getCloudInt(lastBalloonColor, false), ClientProxy.getCloudInt(lastBalloonColor, true), false);
            Component slotsPublic = Component.translatable("cloudstorage.container.balloon_stand.slots", ClientProxy.getStaticCloudInt(lastBalloonColor, false), ClientProxy.getStaticCloudInt(lastBalloonColor, true), false);
            if(font.width(slotsPrivate) > 78){
                slotsPrivate = Component.literal(ClientProxy.getCloudInt(lastBalloonColor, false) + " / " + ClientProxy.getCloudInt(lastBalloonColor, true));
            }
            if(font.width(slotsPublic) > 78){
                slotsPublic = Component.literal(ClientProxy.getStaticCloudInt(lastBalloonColor, false) + " / " + ClientProxy.getStaticCloudInt(lastBalloonColor, true));
            }
            guiGraphics.drawString(font, Component.translatable("cloudstorage.container.balloon_stand.private_slots"), textX, textY + 20, textColor, false);
            guiGraphics.drawString(font, slotsPrivate, textX, textY + 30, textColor, false);
            guiGraphics.drawString(font, Component.translatable("cloudstorage.container.balloon_stand.public_slots"), textX, textY + 40, textColor, false);
            guiGraphics.drawString(font, slotsPublic, textX, textY + 50, textColor, false);
            guiGraphics.pose().popPose();
        }
    }

}