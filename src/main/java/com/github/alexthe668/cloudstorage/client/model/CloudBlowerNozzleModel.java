package com.github.alexthe668.cloudstorage.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CloudBlowerNozzleModel extends AdvancedEntityModel {
    private final AdvancedModelBox root;
    private final AdvancedModelBox lever;

    public CloudBlowerNozzleModel() {
        textureWidth = 32;
        textureHeight = 32;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        root.setTextureOffset(0, 0).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);
        root.setTextureOffset(0, 11).addBox(-3.0F, -9.0F, -3.0F, 6.0F, 2.0F, 6.0F, 0.0F, false);

        lever = new AdvancedModelBox(this);
        lever.setRotationPoint(0.0F, -8.0F, 0.0F);
        root.addChild(lever);
        lever.setTextureOffset(12, 27).addBox(0.0F, 3.0F, 2.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(Entity entity, float v, float v1, float v2, float v3, float v4) {
        this.resetToDefaultPose();
    }

    public void animateInHand(float useTime, ItemTransforms.TransformType transformType) {
        resetToDefaultPose();
        float div = 5F;
        float useProgress = Math.min(div, useTime) / div;
        float forwards = 60F;
        progressPositionPrev(lever, useProgress, 0F, 2F, 0F, 1F);
        if(transformType.firstPerson()){
            forwards = 80F;
            progressPositionPrev(root, useProgress, 0, -5, 2, 1F);
        }
        progressRotationPrev(root, useProgress, (float) Math.toRadians(forwards), 0, 0, 1F);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, lever);
    }

}