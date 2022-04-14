package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.client.model.BalloonModel;
import com.github.alexthe668.cloudstorage.client.model.BloviatorModel;
import com.github.alexthe668.cloudstorage.client.model.CloudChestModel;
import com.github.alexthe668.cloudstorage.entity.BalloonFace;
import com.github.alexthe668.cloudstorage.item.BalloonBuddyItem;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class CSItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final ResourceLocation CLOUD_CHEST_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/cloud_chest.png");
    public static final ResourceLocation CLOUD_CHEST_LIGHTNING_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/cloud_chest_static.png");
    public static final ResourceLocation BLOVIATOR_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/bloviator/bloviator.png");
    private static final BalloonModel BALLOON_MODEL = new BalloonModel();
    private static final CloudChestModel CLOUD_CHEST_MODEL = new CloudChestModel();
    private static final BloviatorModel BLOVIATOR_MODEL = new BloviatorModel(5);
    private static Vec3 lightningZapPos = Vec3.ZERO;
    private static final Random random = new Random();
    private static int tickForRender = 0;
    private static int changeFaceTime = 0;
    private static BalloonFace cyclingBalloonFace = BalloonFace.HAPPY;
    private final LightningRender lightningRender = new LightningRender();
    private final LightningBoltData.BoltRenderInfo lightningBoltData = new LightningBoltData.BoltRenderInfo(0.5F, 0.1F, 0.5F, 0.85F, new Vector4f(0.1F, 0.3F, 0.3F, 1.0F), 0.1F);

    public CSItemRenderer() {
        super(null, null);
    }

    public static void incrementRenderTick() {
        tickForRender++;
        if(changeFaceTime-- <= 0){
            changeFaceTime = 40;
            int nextOrdinal = cyclingBalloonFace.ordinal() + 1;
            if(nextOrdinal < BalloonFace.values().length){
                cyclingBalloonFace = BalloonFace.values()[nextOrdinal];
            }else {
                cyclingBalloonFace = BalloonFace.HAPPY;
            }
        }
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float partialTick = Minecraft.getInstance().getFrameTime();
        float ageInTicks;
        if (!Minecraft.getInstance().isPaused() && Minecraft.getInstance().player != null) {
            ageInTicks = tickForRender + partialTick;
        } else {
            tickForRender = Minecraft.getInstance().player.tickCount;
            ageInTicks = tickForRender + partialTick;
        }
        if (Util.getMillis() % 100 == 0) {
            Vec3 raw = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            lightningZapPos = raw.normalize().scale(5F);
        }
        if (itemStackIn.getItem() instanceof BalloonItem) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if (BalloonItem.get3DRender(itemStackIn) != 0 || transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
                int color = ((DyeableLeatherItem) itemStackIn.getItem()).getColor(itemStackIn);
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                Vec3 swingVec = new Vec3(Math.sin(ageInTicks * 0.1F) * 0.2F, Math.cos(ageInTicks * 0.1F + 2F) * 0.3F, Math.cos(ageInTicks * 0.1F) * 0.2F);
                Vec3 to = new Vec3(0, 2F, 0);
                BALLOON_MODEL.setColor(r, g, b);
                if (transformType == ItemTransforms.TransformType.GUI) {
                    if (BalloonItem.get3DRender(itemStackIn) == 1) {
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-135F));
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-15F));
                        matrixStackIn.translate(0, -0.9F, 0);
                        swingVec = swingVec.scale(0.2F);
                        to = to.scale(0.8F);
                    } else {
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(15));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-135));
                        matrixStackIn.translate(0, -0.9F, 0);
                        swingVec = swingVec.scale(0.2F);
                        to = to.scale(1F);
                    }
                }
                Vec3 from = new Vec3(swingVec.x, 1.3F + swingVec.y, swingVec.z);
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-180F));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180F));
                matrixStackIn.translate(0, -2F, 0);
                StringRenderHelper.renderSting(Minecraft.getInstance().player, from, partialTick, matrixStackIn, bufferIn, to);
                matrixStackIn.translate(swingVec.x, swingVec.y, swingVec.z);
                BALLOON_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(BalloonTextures.BALLOON)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                if (itemStackIn.is(CSItemRegistry.BALLOON_BUDDY.get())) {
                    BalloonFace face = BalloonBuddyItem.getPersonality(itemStackIn);
                    if (face == null) {
                        face = cyclingBalloonFace;
                    }
                    if (face != null) {
                        BALLOON_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(BalloonTextures.getTextureForFace(face))), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 0.75F);
                    }
                }
                BALLOON_MODEL.setColor(1.0F, 1.0F, 1.0F);
                BALLOON_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(BalloonTextures.STRING_TIE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                BALLOON_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(BalloonTextures.BALLOON_SHEEN)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 0.75F);
                matrixStackIn.popPose();

            } else {
                ItemStack copyColorData = new ItemStack(itemStackIn.is(CSItemRegistry.BALLOON_BUDDY.get()) ? CSItemRegistry.BALLOON_BUDDY_INVENTORY.get() : CSItemRegistry.BALLOON_INVENTORY.get());
                copyColorData.setTag(itemStackIn.getTag());
                Minecraft.getInstance().getItemRenderer().renderStatic(copyColorData, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);

            }
        }
        if (itemStackIn.is(CSBlockRegistry.CLOUD_CHEST.get().asItem())) {
            CompoundTag tag = itemStackIn.getTag();
            if (tag != null && tag.getBoolean("MobRender")) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5F, 1.5F, 0.5F);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-180F));
                matrixStackIn.translate(0.3F, 0.7F, 0);
                matrixStackIn.scale(0.55F, 0.55F, 0.55F);
                BLOVIATOR_MODEL.resetToDefaultPose();
                BLOVIATOR_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(BLOVIATOR_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            } else {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5F, 1.5F, 0.5F);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-180F));
                CLOUD_CHEST_MODEL.resetToDefaultPose();
                CLOUD_CHEST_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(CLOUD_CHEST_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            }
        }
        if (itemStackIn.is(CSBlockRegistry.STATIC_CLOUD_CHEST.get().asItem())) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-180F));
            CLOUD_CHEST_MODEL.resetToDefaultPose();
            CLOUD_CHEST_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(CLOUD_CHEST_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            CLOUD_CHEST_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.dragonExplosionAlpha(CLOUD_CHEST_LIGHTNING_TEXTURE)), 240, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
    }
}
