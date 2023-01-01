package com.github.alexthe668.cloudstorage.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe668.cloudstorage.entity.BloviatorEntity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

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
        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, -6.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -10.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-13.0F, -11.0F, -10.0F, 26.0F, 21.0F, 31.0F, 0.0F, false);

        clouds = new AdvancedModelBox[cloudCount];
        for (int i = 0; i < cloudCount; i++) {
            float f = cloudCount <= 1 ? 0F : ((i) / (float) (cloudCount - 1)) - 0.5F;
            float f2 = clouds.length <= 1 ? 0.25F : Mth.cos((float) (f * Math.PI));
            AdvancedModelBox trail = new AdvancedModelBox(this, "cloud_" + i);
            trail.setRotationPoint(f * 48F, -2.5F, 19F + f2 * 22F);
            trail.setTextureOffset(41, 53).addBox(-5.5F, -5.5F, -5.5F, 11.0F, 11.0F, 11.0F, 0.0F, false);
            body.addChild(trail);
            clouds[i] = trail;
        }

        left_cheek = new AdvancedModelBox(this, "left_cheek");
        left_cheek.setRotationPoint(14.5F, -1.0F, 1.5F);
        body.addChild(left_cheek);
        left_cheek.setTextureOffset(0, 53).addBox(-1.5F, -7.0F, -7.5F, 5.0F, 14.0F, 15.0F, 0.0F, false);

        right_cheek = new AdvancedModelBox(this, "right_cheek");
        right_cheek.setRotationPoint(-14.5F, -1.0F, 1.5F);
        body.addChild(right_cheek);
        right_cheek.setTextureOffset(0, 53).addBox(-3.5F, -7.0F, -7.5F, 5.0F, 14.0F, 15.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(BloviatorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = ageInTicks - entity.tickCount;
        float idleSpeed = 0.15F;
        float idleDegree = 1F;
        float cheekProgress = entity.getPushProgress(partialTick);
        float cheekS = 1F;
        this.progressPositionPrev(right_cheek, cheekProgress, -1, 0F, -3, 1F);
        this.progressPositionPrev(left_cheek, cheekProgress, 1, 0F, -3, 1F);
        this.right_cheek.setScale(1F + cheekProgress, 1F, 1F);
        this.left_cheek.setScale(1F + cheekProgress, 1F, 1F);
        this.bob(body, idleSpeed, idleDegree * 0.5F, false, ageInTicks, 1);
        right_cheek.rotationPointX += cheekS * (idleDegree + Math.sin(ageInTicks * idleSpeed - 1F) * idleDegree);
        left_cheek.rotationPointX -= cheekS * (idleDegree + Math.sin(ageInTicks * idleSpeed - 1F) * idleDegree);
        right_cheek.rotationPointY += cheekS * Math.sin(ageInTicks * idleSpeed) * idleDegree;
        left_cheek.rotationPointY += cheekS * Math.sin(ageInTicks * idleSpeed + 1.5F) * idleDegree;
        right_cheek.rotationPointZ -= cheekS * Math.sin(ageInTicks * idleSpeed + 1.5F) * idleDegree;
        left_cheek.rotationPointZ += cheekS * Math.sin(ageInTicks * idleSpeed + 3F) * idleDegree;
        float renderYaw = (float)entity.getLatencyVar(0, 3, partialTick);
        float half = clouds.length / 2F;
        double d0 = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double d1 = Mth.lerp(partialTick, entity.yOld, entity.getY());
        double d2 = Mth.lerp(partialTick, entity.zOld, entity.getZ());
        float cloudOffset = 10F;
        for (int i = 0; i < clouds.length; i++) {
            float f = clouds.length <= 1 ? 0F : ((i) / (float) (clouds.length - 1)) - 0.5F;
            int offsetLatency = 5 + (int)(Mth.cos((float) (f * Math.PI)) * 15F);
            AdvancedModelBox cloud = clouds[i];
            Vec3 vec1 = new Vec3(entity.getLatencyVar(offsetLatency, 0, partialTick) - d0, entity.getLatencyVar(offsetLatency, 1, partialTick) - d1, entity.getLatencyVar(offsetLatency, 2, partialTick) - d2).yRot(renderYaw * ((float)Math.PI / 180F));
            float intensity = 1.0F + (float) vec1.length() * 0.4F;
            cloud.rotationPointX += Math.sin(ageInTicks * idleSpeed + i * 1.3F - half) * idleDegree * intensity + vec1.x * -cloudOffset;
            cloud.rotationPointY += Math.sin(ageInTicks * idleSpeed + i * 2F - 1F) * idleDegree * intensity * 2.0F + vec1.y * -cloudOffset;
            cloud.rotationPointZ += Math.cos(ageInTicks * idleSpeed + i * 1.6F - half) * idleDegree * intensity * 2F + vec1.z * -cloudOffset;
            cloud.rotateAngleY += Math.toRadians(entity.getLatencyVar(offsetLatency / 2, 3, partialTick) - renderYaw);
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