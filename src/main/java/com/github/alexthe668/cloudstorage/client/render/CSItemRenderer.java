package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.client.model.BalloonModel;
import com.github.alexthe668.cloudstorage.client.model.BloviatorModel;
import com.github.alexthe668.cloudstorage.client.model.CloudBlowerNozzleModel;
import com.github.alexthe668.cloudstorage.client.model.CloudChestModel;
import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.entity.BalloonFace;
import com.github.alexthe668.cloudstorage.item.BalloonBuddyItem;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.item.CloudBlowerItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Vector4f;

import java.util.Random;

public class CSItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final ResourceLocation CLOUD_CHEST_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/cloud_chest.png");
    public static final ResourceLocation CLOUD_CHEST_LIGHTNING_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/cloud_chest_static.png");
    public static final ResourceLocation BLOVIATOR_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/bloviator/bloviator.png");
    public static final ResourceLocation CLOUD_BLOWER_NOZZLE_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/cloud_blower_nozzle.png");
    private static final BalloonModel BALLOON_MODEL = new BalloonModel();
    private static final CloudChestModel CLOUD_CHEST_MODEL = new CloudChestModel();
    private static final BloviatorModel BLOVIATOR_MODEL = new BloviatorModel(5);
    private static final CloudBlowerNozzleModel CLOUD_BLOWER_NOZZLE_MODEL = new CloudBlowerNozzleModel();
    private static Vec3 lightningZapPos = Vec3.ZERO;
    private static final Random random = new Random();
    private static int tickForRender = 0;
    private static float ageInTicks = 0;
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

    public static void renderBalloonStatic(LivingEntity player, ItemStack balloon, boolean leftHanded) {
        int i = leftHanded ? -1 : 1;
        float f = player.getAttackAnim(1.0F);
        float f1 = Mth.sin(Mth.sqrt(f) * (float)Math.PI);
        float f2 = player.yBodyRot * ((float)Math.PI / 180F);
        double d0 = (double)Mth.sin(f2);
        double d1 = (double)Mth.cos(f2);
        double d2 = (double)i * 0.35D;
        double d4;
        double d5;
        double d6;
        float f3;
        float ageInTicksOffset = player.tickCount;
        Vec3 swingVec = new Vec3(Math.sin(ageInTicksOffset * 0.1F) * 0.2F, Math.cos(ageInTicksOffset * 0.1F + 2F) * 0.3F, Math.cos(ageInTicksOffset * 0.1F) * 0.2F);
        if ((Minecraft.getInstance().getEntityRenderDispatcher().options == null || Minecraft.getInstance().getEntityRenderDispatcher().options.getCameraType().isFirstPerson()) && player == Minecraft.getInstance().player) {
            double d7 = 960.0D / Minecraft.getInstance().getEntityRenderDispatcher().options.fov().get();
            Vec3 vec3 = Minecraft.getInstance().getEntityRenderDispatcher().camera.getNearPlane().getPointOnPlane((float)i * 0.525F, -0.1F);
            vec3 = vec3.scale(d7);
            vec3 = vec3.yRot(f1 * 0.5F);
            vec3 = vec3.xRot(-f1 * 0.7F);
            d4 = player.getX() + vec3.x;
            d5 = player.getY() + vec3.y;
            d6 = player.getZ() + vec3.z;
            f3 = player.getEyeHeight();
        } else {
            d4 = player.getX() - d1 * d2 - d0 * 0.8D;
            d5 = player.getEyeHeight() + player.getY() - 0.45D;
            d6 = player.getZ() - d0 * d2 + d1 * 0.8D;
            f3 = player.isCrouching() ? -0.1875F : 0.0F;
        }
        float f4 = (random.nextFloat() - 0.5F) * 0.5F;
        float f5 = (random.nextFloat() - 0.5F) * 0.5F;
        float f6 = (random.nextFloat() - 0.5F) * 0.5F;
        float f7 = 1.2F;
        player.level.addParticle(CSParticleRegistry.STATIC_LIGHTNING.get(), d4 - swingVec.x + f4, d5 + f7 + f3 - swingVec.y + f5, d6 - swingVec.z + f5, f4 * 0.5F, f5 * 0.5F, f6 * 0.5F);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float partialTick = Minecraft.getInstance().getFrameTime();
        if (!Minecraft.getInstance().isPaused() && Minecraft.getInstance().player != null) {
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
                int colorIn = ((DyeableLeatherItem) itemStackIn.getItem()).getColor(itemStackIn);
                int color = colorIn < 0 ? getRainbowBalloonColor() : colorIn;
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                Vec3 swingVec = new Vec3(Math.sin(ageInTicks * 0.1F) * 0.2F, Math.cos(ageInTicks * 0.1F + 2F) * 0.3F, Math.cos(ageInTicks * 0.1F) * 0.2F);
                Vec3 to = new Vec3(0, 2F, 0);
                BALLOON_MODEL.setColor(r, g, b);
                if (transformType == ItemTransforms.TransformType.GUI) {
                    if (BalloonItem.get3DRender(itemStackIn) == 1) {
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-135F));
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-15F));
                        matrixStackIn.translate(0, -0.9F, 0);
                        swingVec = swingVec.scale(0.2F);
                        to = to.scale(0.8F);
                    } else {
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(15));
                        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-135));
                        matrixStackIn.translate(0, -0.9F, 0);
                        swingVec = swingVec.scale(0.2F);
                        to = to.scale(1F);
                    }
                }
                Vec3 from = new Vec3(swingVec.x, 1.3F + swingVec.y, swingVec.z);
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180F));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180F));
                matrixStackIn.translate(0, -2F, 0);
                StringRenderHelper.renderSting(Minecraft.getInstance().player, from, partialTick, matrixStackIn, bufferIn, to, combinedLightIn);
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
                if (BalloonItem.isLoot(itemStackIn)) {
                    BALLOON_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(BalloonTextures.LOOT)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                }
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
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180F));
                matrixStackIn.translate(0.3F, 0.7F, 0);
                matrixStackIn.scale(0.55F, 0.55F, 0.55F);
                BLOVIATOR_MODEL.resetToDefaultPose();
                BLOVIATOR_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(BLOVIATOR_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            } else {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5F, 1.5F, 0.5F);
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180F));
                CLOUD_CHEST_MODEL.resetToDefaultPose();
                CLOUD_CHEST_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(CLOUD_CHEST_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            }
        }
        if (itemStackIn.is(CSBlockRegistry.STATIC_CLOUD_CHEST.get().asItem())) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180F));
            CLOUD_CHEST_MODEL.resetToDefaultPose();
            CLOUD_CHEST_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(CLOUD_CHEST_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            CLOUD_CHEST_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(ForgeRenderTypes.getUnlitTranslucent(CLOUD_CHEST_LIGHTNING_TEXTURE)), 240, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
        if(itemStackIn.is(CSItemRegistry.CLOUD_BLOWER.get())) {
            if (transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.45F, 1.8F, 0.5F);
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180F));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180F));
                float useTime = CloudBlowerItem.getLerpedUseTime(itemStackIn, partialTick);
                float leverForwards = CloudBlowerItem.getMode(itemStackIn);
                CLOUD_BLOWER_NOZZLE_MODEL.animateInHand(useTime, leverForwards, transformType);
                CLOUD_BLOWER_NOZZLE_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(CLOUD_BLOWER_NOZZLE_TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                if(useTime > 5F){
                    Vec3 beam = new Vec3(0, 0, 3F);
                    matrixStackIn.pushPose();
                    if(transformType.firstPerson()){
                        matrixStackIn.translate(0, 1.1F, -0.4F);
                    }else{
                        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-20F));
                        matrixStackIn.translate(0, 1.3F, -0.1F);
                    }
                    float intensity = leverForwards == 1 ? -1.0F : 1.0F;
                    StringRenderHelper.renderCloudBlowerBeam((float) beam.x, (float) beam.y, (float) beam.z, partialTick, CloudBlowerItem.getUseTime(itemStackIn), matrixStackIn, bufferIn, combinedLightIn, intensity, 1F, false);
                    matrixStackIn.popPose();
                }
                matrixStackIn.popPose();
            } else {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5F, 0.5f, 0.5f);
                ItemStack cloudBlower = new ItemStack(CSItemRegistry.CLOUD_BLOWER_INVENTORY.get());
                Minecraft.getInstance().getItemRenderer().renderStatic(cloudBlower, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
                matrixStackIn.popPose();

            }
        }
    }

    public static int getRainbowBalloonColor(){
        return (int) (ageInTicks % 0xFFFFFF);
    }
}
