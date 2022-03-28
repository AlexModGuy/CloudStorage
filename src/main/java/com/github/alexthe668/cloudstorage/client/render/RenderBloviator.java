package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe668.cloudstorage.client.model.BalloonModel;
import com.github.alexthe668.cloudstorage.client.model.BloviatorModel;
import com.github.alexthe668.cloudstorage.entity.BadloonEntity;
import com.github.alexthe668.cloudstorage.entity.BadloonHandEntity;
import com.github.alexthe668.cloudstorage.entity.BloviatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

public class RenderBloviator extends MobRenderer<BloviatorEntity, BloviatorModel> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("cloudstorage:textures/entity/bloviator/bloviator.png");
    public RenderBloviator(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BloviatorModel(4), 0.3F);
    }

    protected float getFlipDegrees(BloviatorEntity entity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(BloviatorEntity entity) {
        return TEXTURE;
    }
}
