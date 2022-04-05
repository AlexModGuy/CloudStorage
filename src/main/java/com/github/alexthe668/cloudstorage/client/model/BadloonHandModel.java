package com.github.alexthe668.cloudstorage.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe668.cloudstorage.entity.BadloonHandEntity;
import com.github.alexthe668.cloudstorage.entity.GloveGesture;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;

public class BadloonHandModel extends AdvancedEntityModel<BadloonHandEntity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox hand;
    private final AdvancedModelBox lip;
    private final AdvancedModelBox palm;
    private final AdvancedModelBox thumb;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox indexfinger;
    private final AdvancedModelBox middlefinger;
    private final AdvancedModelBox ringfinger;

    public BadloonHandModel() {
        textureWidth = 32;
        textureHeight = 32;
        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 12.0F, 0.0F);

        hand = new AdvancedModelBox(this);
        hand.setRotationPoint(0.0F, 12.0F, 0.0F);
        hand.rotateAngleY = -1.5708F;
        root.addChild(hand);
        lip = new AdvancedModelBox(this);
        lip.setRotationPoint(0.0F, 0.0F, 0.0F);
        hand.addChild(lip);
        lip.setTextureOffset(0, 12).addBox(-2.5F, -12.0F, -3.0F, 4.0F, 1.0F, 6.0F, 0.0F, false);
        lip.setTextureOffset(14, 12).addBox(-2.0F, -11.0F, -2.0F, 3.0F, 1.0F, 4.0F, 0.0F, false);

        palm = new AdvancedModelBox(this);
        palm.setRotationPoint(0.0F, 0.0F, 0.0F);
        hand.addChild(palm);
        palm.setTextureOffset(0, 0).addBox(-2.5F, -10.0F, -4.0F, 4.0F, 4.0F, 8.0F, 0.0F, false);

        thumb = new AdvancedModelBox(this);
        thumb.setRotationPoint(-1.0F, -8.0F, 3.5F);
        hand.addChild(thumb);


        cube_r1 = new AdvancedModelBox(this);
        cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        thumb.addChild(cube_r1);
        cube_r1.setTextureOffset(16, 0).addBox(-0.75F, -1.0F, 0.25F, 2.0F, 2.0F, 3.0F, 0.0F, false);

        indexfinger = new AdvancedModelBox(this);
        indexfinger.setRotationPoint(-1.0F, -7.0F, 2.75F);
        hand.addChild(indexfinger);
        indexfinger.setTextureOffset(0, 19).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        middlefinger = new AdvancedModelBox(this);
        middlefinger.setRotationPoint(-1.0F, -7.0F, 0.0F);
        hand.addChild(middlefinger);
        middlefinger.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);

        ringfinger = new AdvancedModelBox(this);
        ringfinger.setRotationPoint(-1.0F, -7.0F, -2.75F);
        hand.addChild(ringfinger);
        ringfinger.setTextureOffset(18, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, hand, lip, palm, thumb, cube_r1, indexfinger, middlefinger, ringfinger);
    }

    @Override
    public void setupAnim(BadloonHandEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        root.setRotationPoint(0.0F, 0.0F, 0.0F);
        hand.setRotationPoint(0.0F, 12.0F, 0.0F);
        float partialTicks = ageInTicks - entity.tickCount;
        float gestureProgress = entity.prevGestureProgress + (entity.gestureProgress - entity.prevGestureProgress) * partialTicks;
        animateForGesture(entity.getPrevGesture(), ageInTicks, 1F - gestureProgress);
        animateForGesture(entity.getGesture(), ageInTicks, gestureProgress);
    }

    private void animateForGesture(GloveGesture gesture, float ageInTicks, float progress) {
        float twiddleSpeed = 0.15F;
        float twiddleDegree = 0.3F;
        float indexScale = gesture == GloveGesture.POINT ? 1.5F : 1F;
        float middleScale = gesture == GloveGesture.FLIPOFF ? 1.5F : 1F;
        this.indexfinger.setScale(1F, indexScale, 1F);
        this.middlefinger.setScale(1F, middleScale, 1F);
        if (gesture == GloveGesture.GRAB) {
            progressPositionPrev(root, progress, 0, 2, 2, 1F);
            progressRotationPrev(root, progress, (float) Math.toRadians(-90F), 0, 0, 1F);
            progressRotationPrev(thumb, progress, 0, (float) Math.toRadians(50F), (float) Math.toRadians(20F), 1F);
            progressRotationPrev(indexfinger, progress, (float) Math.toRadians(20F), 0, (float) Math.toRadians(-20F), 1F);
            progressRotationPrev(middlefinger, progress, 0, 0, (float) Math.toRadians(-20F), 1F);
            progressRotationPrev(ringfinger, progress, (float) Math.toRadians(-20F), 0, (float) Math.toRadians(-20F), 1F);
        } else if (gesture == GloveGesture.WAVE) {
            progressPositionPrev(root, progress, 0, 2, 0, 1F);
            progressPositionPrev(thumb, progress, 0, -1, 0, 1F);
            progressRotationPrev(root, progress, (float) Math.toRadians(-170F), 0, 0, 1F);
            progressRotationPrev(thumb, progress, (float) Math.toRadians(-70F), 0, 0, 1F);
            this.flap(root, 0.5F, 0.5F, false, 0, 0F, ageInTicks, progress);
        } else if (gesture == GloveGesture.PUNCH) {
            progressRotationPrev(root, progress, (float) Math.toRadians(-90F), 0, 0, 1F);
            progressRotationPrev(middlefinger, progress, 0, 0, (float) Math.toRadians(-110F), 1F);
            progressRotationPrev(indexfinger, progress, 0, 0, (float) Math.toRadians(-110F), 1F);
            progressRotationPrev(ringfinger, progress, 0, 0, (float) Math.toRadians(-110F), 1F);
            progressPositionPrev(middlefinger, progress, 0, 1F, 0, 1F);
            progressPositionPrev(indexfinger, progress, 0, 1.25F, 0, 1F);
            progressPositionPrev(ringfinger, progress, 0, 1F, 0, 1F);
            progressRotationPrev(thumb, progress, 0, (float) Math.toRadians(90F), 0, 1F);
        }else if (gesture == GloveGesture.POINT) {
            progressPositionPrev(root, progress, 0, 2, 2, 1F);
            progressRotationPrev(root, progress, (float) Math.toRadians(-90F), 0, (float) Math.toRadians(90F), 1F);
            progressPositionPrev(middlefinger, progress, 0, 1.25F, 0, 1F);
            progressPositionPrev(ringfinger, progress, -1, 1.25F, 0, 1F);
            progressRotationPrev(middlefinger, progress, 0, 0, (float) Math.toRadians(-110F), 1F);
            progressRotationPrev(ringfinger, progress, 0, 0, (float) Math.toRadians(-110F), 1F);
        } else if (gesture == GloveGesture.FLIPOFF) {
            progressPositionPrev(root, progress, 0, 2, 0, 1F);
            progressPositionPrev(thumb, progress, 0, -1, 1, 1F);
            progressRotationPrev(root, progress, (float) Math.toRadians(-170F), (float) Math.toRadians(-180F), 0, 1F);
            progressRotationPrev(ringfinger, progress, 0, 0, (float) Math.toRadians(-110F), 1F);
            progressPositionPrev(ringfinger, progress, 0, 1.25F, 0, 1F);
            progressRotationPrev(indexfinger, progress, 0, 0, (float) Math.toRadians(-110F), 1F);
            progressPositionPrev(indexfinger, progress, 0, 1.25F, 0, 1F);
            progressRotationPrev(thumb, progress, 0, (float) Math.toRadians(90F), 0, 1F);
        } else {
            this.walk(thumb, twiddleSpeed, twiddleDegree, false, 0, -0.3F, ageInTicks, progress);
            this.flap(indexfinger, twiddleSpeed, twiddleDegree, false, 1, -0.3F, ageInTicks, progress);
            this.flap(middlefinger, twiddleSpeed, twiddleDegree, false, 2, -0.3F, ageInTicks, progress);
            this.flap(ringfinger, twiddleSpeed, twiddleDegree, false, 3, -0.3F, ageInTicks, progress);
        }
    }

    public void applyTransformsToItem(PoseStack poseStack){
        root.translateRotate(poseStack);
        hand.translateRotate(poseStack);
        palm.translateRotate(poseStack);
    }
}