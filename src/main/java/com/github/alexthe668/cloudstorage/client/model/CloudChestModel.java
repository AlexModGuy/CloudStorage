package com.github.alexthe668.cloudstorage.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe668.cloudstorage.block.AbstractCloudChestBlockEntity;
import com.github.alexthe668.cloudstorage.block.CloudChestBlockEntity;
import com.github.alexthe668.cloudstorage.entity.BadloonEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonFace;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.world.entity.Entity;

public class CloudChestModel<T extends Entity> extends AdvancedEntityModel<T> {
    private final AdvancedModelBox bottom;
    private final AdvancedModelBox lid;

    private float r = 1.0F;
    private float g = 1.0F;
    private float b = 1.0F;

    public CloudChestModel() {
        texWidth = 64;
        texHeight = 64;



        bottom = new AdvancedModelBox(this);
        bottom.setPos(7.0F, 24.0F, -7.0F);
        bottom.setTextureOffset(0, 19).addBox(-14.0F, -10.0F, 0.0F, 14.0F, 10.0F, 14.0F, 0.0F, false);

        lid = new AdvancedModelBox(this);
        lid.setPos(-7.0F, -10.0F, 14.0F);
        bottom.addChild(lid);
        lid.setTextureOffset(0, 0).addBox(-7.0F, -4.0F, -14.0F, 14.0F, 5.0F, 14.0F, 0.0F, false);
        lid.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -15.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);

        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(bottom);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(bottom, lid);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
    }

    public void renderChest(AbstractCloudChestBlockEntity entity, float partialTicks) {
        this.resetToDefaultPose();
        float open = entity.getOpenProgress(partialTicks);
        this.lid.rotateAngleX -= open * (float)Math.PI * 0.5F;
        this.walk(lid, 0.2F, 0.1F, false, 2F, -0.1F, entity.tickCount + partialTicks, open);
    }
}
