package com.github.alexthe668.cloudstorage.client.gui;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.client.ClientProxy;
import com.github.alexthe668.cloudstorage.inventory.BalloonStandMenu;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.network.MessageRequestCloudInfo;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
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

    public void render(PoseStack stack, int x, int y, float partialTick) {
        this.renderBackground(stack);
        this.renderBg(stack, partialTick, x, y);
        super.render(stack, x, y, partialTick);
        this.renderTooltip(stack, x, y);
    }

    protected void renderBg(PoseStack poseStack, float f, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        float partialTicks = Minecraft.getInstance().getFrameTime();
        float cloud = (prevCloudProgress + (cloudProgress - prevCloudProgress) * partialTicks) / 10F;
        int cloudTexture = tickCount / 7;
        renderLittleCloud(poseStack, partialTicks, cloud * 0.8F, cloudTexture);
    }

    private void renderLittleCloud(PoseStack poseStack, float partialTick, float alpha, int cloudTexture){
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        this.blit(poseStack, i + 7, j + 15, 176, 66, 66, 66);
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

    protected void renderLabels(PoseStack poseStack, int x, int y) {
        float cloud = prevCloudProgress + (cloudProgress - prevCloudProgress) * minecraft.getFrameTime();
        this.font.draw(poseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
        this.font.draw(poseStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY + 10, 4210752);
        int alpha = (int) ((cloud / 10F) * 255);
        if (alpha > 10) {
            int fade = FastColor.ARGB32.color(alpha, 255, 255, 255);
            poseStack.pushPose();
            poseStack.scale(0.8F, 0.8F, 0.8F);
            String color = Integer.toHexString(lastBalloonColor).toUpperCase(Locale.ROOT) + " RGB";
            int textColor = FastColor.ARGB32.multiply(0X85A2B2, fade);
            float textX = 20;
            float textY = 30;
            int r = (int) (lastBalloonColor >> 16 & 255);
            int g = (int) (lastBalloonColor >> 8 & 255);
            int b = (int) (lastBalloonColor & 255);
            this.font.draw(poseStack, Component.translatable("cloudstorage.container.balloon_stand.color"), textX, textY, textColor);
            this.font.draw(poseStack, color, textX, textY + 10, FastColor.ARGB32.color(alpha, r, g, b));
            Component slotsPrivate = Component.translatable("cloudstorage.container.balloon_stand.slots", ClientProxy.getCloudInt(lastBalloonColor, false), ClientProxy.getCloudInt(lastBalloonColor, true));
            Component slotsPublic = Component.translatable("cloudstorage.container.balloon_stand.slots", ClientProxy.getStaticCloudInt(lastBalloonColor, false), ClientProxy.getStaticCloudInt(lastBalloonColor, true));
            if(font.width(slotsPrivate) > 78){
                slotsPrivate = Component.literal(ClientProxy.getCloudInt(lastBalloonColor, false) + " / " + ClientProxy.getCloudInt(lastBalloonColor, true));
            }
            if(font.width(slotsPublic) > 78){
                slotsPublic = Component.literal(ClientProxy.getStaticCloudInt(lastBalloonColor, false) + " / " + ClientProxy.getStaticCloudInt(lastBalloonColor, true));
            }
            this.font.draw(poseStack, Component.translatable("cloudstorage.container.balloon_stand.private_slots"), textX, textY + 20, textColor);
            this.font.draw(poseStack, slotsPrivate, textX, textY + 30, textColor);
            this.font.draw(poseStack, Component.translatable("cloudstorage.container.balloon_stand.public_slots"), textX, textY + 40, textColor);
            this.font.draw(poseStack, slotsPublic, textX, textY + 50, textColor);
            poseStack.popPose();
        }
    }

}