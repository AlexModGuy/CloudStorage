package com.github.alexthe668.cloudstorage.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe668.cloudstorage.entity.BloviatorEntity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.Mth;

import java.util.Arrays;

public class BloviatorModel extends AdvancedEntityModel<BloviatorEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox left_cheek;
    private final AdvancedModelBox right_cheek;
    private final AdvancedModelBox[] clouds;

    public BloviatorModel(int cloudCount) {
        texWidth = 128;
        texHeight = 128;
        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, -6.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -10.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-13.0F, -11.0F, -10.0F, 26.0F, 21.0F, 31.0F, 0.0F, false);

        clouds = new AdvancedModelBox[cloudCount];
        for (int i = 0; i < cloudCount; i++) {
            float f = cloudCount <= 1 ? 0F : ((i) / (float) (cloudCount - 1)) - 0.5F;
            float f2 = Mth.cos((float) (f * Math.PI * 2F));
            AdvancedModelBox trail = new AdvancedModelBox(this);
            trail.setRotationPoint(f * 50F, -2.5F, 23F + f2 * 15F);
            trail.setTextureOffset(41, 53).addBox(-5.5F, -5.5F, -5.5F, 11.0F, 11.0F, 11.0F, 0.0F, false);
            body.addChild(trail);
            clouds[i] = trail;
        }

        left_cheek = new AdvancedModelBox(this);
        left_cheek.setRotationPoint(14.5F, -1.0F, 1.5F);
        body.addChild(left_cheek);
        left_cheek.setTextureOffset(0, 53).addBox(-1.5F, -7.0F, -7.5F, 5.0F, 14.0F, 15.0F, 0.0F, false);

        right_cheek = new AdvancedModelBox(this);
        right_cheek.setRotationPoint(-14.5F, -1.0F, 1.5F);
        body.addChild(right_cheek);
        right_cheek.setTextureOffset(0, 53).addBox(-3.5F, -7.0F, -7.5F, 5.0F, 14.0F, 15.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(BloviatorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.15F;
        float idleDegree = 1F;
        float cheekS = 1F;
        this.bob(body, idleSpeed, idleDegree * 0.5F, false, ageInTicks, 1);
        right_cheek.rotationPointX += cheekS * (idleDegree + Math.sin(ageInTicks * idleSpeed - 1F) * idleDegree);
        left_cheek.rotationPointX -= cheekS * (idleDegree + Math.sin(ageInTicks * idleSpeed - 1F) * idleDegree);
        right_cheek.rotationPointY += cheekS * Math.sin(ageInTicks * idleSpeed) * idleDegree;
        left_cheek.rotationPointY += cheekS * Math.sin(ageInTicks * idleSpeed + 1.5F) * idleDegree;
        right_cheek.rotationPointZ -= cheekS * Math.sin(ageInTicks * idleSpeed + 1.5F) * idleDegree;
        left_cheek.rotationPointZ += cheekS * Math.sin(ageInTicks * idleSpeed + 3F) * idleDegree;
        float half = clouds.length / 2F;
        for (int i = 0; i < clouds.length; i++) {
            AdvancedModelBox cloud = clouds[i];
            cloud.rotationPointX += Math.sin(ageInTicks * idleSpeed + i * 1.3F - half) * idleDegree;
            cloud.rotationPointY += Math.sin(ageInTicks * idleSpeed + i * 2F - 1F) * idleDegree;
            cloud.rotationPointZ += Math.cos(ageInTicks * idleSpeed + i * 1.6F - half) * idleDegree * 2F;
        }
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableSet.<AdvancedModelBox>builder().add(root, body, right_cheek, left_cheek).addAll(Arrays.asList(clouds)).build();
    }
}