package com.github.alexthe668.cloudstorage.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonFace;
import com.github.alexthe668.cloudstorage.entity.LivingBalloon;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector4f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class BalloonModel<T extends Entity> extends AdvancedEntityModel<T> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox balloon;
    private final AdvancedModelBox face;
    private final AdvancedModelBox tie;
    private final AdvancedModelBox below;
    private float r = 1.0F;
    private float g = 1.0F;
    private float b = 1.0F;
    private float a = 1.0F;

    public BalloonModel() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        balloon = new AdvancedModelBox(this, "balloon");
        balloon.setPos(0.0F, 0.0F, 0.0F);
        root.addChild(balloon);
        balloon.setTextureOffset(0, 0).addBox(-5.0F, -15.0F, -5.0F, 10.0F, 10.0F, 10.0F, 0.0F, false);

        face = new AdvancedModelBox(this, "face");
        face.setPos(0.0F, 0.0F, 0.0F);
        balloon.addChild(face);
        face.setTextureOffset(16, 20).addBox(-5.0F, -15.0F, -6.0F, 10.0F, 10.0F, 0.0F, 0.0F, false);

        tie = new AdvancedModelBox(this, "tie");
        tie.setPos(0.0F, 0.0F, 0.0F);
        balloon.addChild(tie);
        tie.setTextureOffset(0, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        below = new AdvancedModelBox(this, "below");
        below.setPos(0.0F, 0.0F, 0.0F);
        tie.addChild(below);
        below.setTextureOffset(0, 20).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 2.0F, 4.0F, 0.0F, false);
        this.balloon.setShouldScaleChildren(true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, balloon, face, tie, below);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float popProgress = 0F;
        if (entity instanceof LivingBalloon badloon) {
            float ability = badloon.getAbilityProgress(ageInTicks - entity.tickCount);
            if (badloon.getFace() == BalloonFace.ANGRY) {
                this.face.rotationPointY += Math.cos(ageInTicks * 0.6F) * 0.7F;
            } else if (badloon.getFace() == BalloonFace.SCARED) {
                this.face.rotationPointX += Math.sin(ageInTicks) * 2F;
            } else if (badloon.getFace() == BalloonFace.SCARY) {
                this.face.rotationPointX += Math.sin(ageInTicks) * 2F * ability;
            } else if (badloon.getFace() == BalloonFace.CRAZY) {
                this.face.rotationPointX += Math.sin(ageInTicks) * 0.5F;
                this.face.rotationPointY += Math.cos(ageInTicks) * 0.5F;
            }  else {
                this.face.rotationPointZ += Math.sin(ageInTicks * 0.3F) * 0.25F;
            }
            popProgress = badloon.getPopProgress(ageInTicks - entity.tickCount);
        }
        if (entity instanceof BalloonEntity balloon) {
            popProgress = balloon.getPopProgress(ageInTicks - entity.tickCount);
        }
        float popScale = 1F + popProgress * 1.7F;
        this.balloon.setScale(popScale, popScale, popScale);
        this.face.setScale(1F / popScale, 1F / popScale, 1F / popScale);
        this.balloon.rotationPointY += popProgress * 17;
        this.face.rotationPointZ -= popProgress * 3F;
        this.face.rotationPointY -= popProgress * 5F;
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1.0F;
    }


    public Vec3 translateToBottom(Vec3 in){
        PoseStack modelTranslateStack = new PoseStack();
        this.root.translateAndRotate(modelTranslateStack);
        this.balloon.translateAndRotate(modelTranslateStack);
        this.tie.translateAndRotate(modelTranslateStack);
        this.below.translateAndRotate(modelTranslateStack);

        Vector4f bodyOffsetVec = new Vector4f((float)in.x, (float)in.y, (float)in.z, 1.0F);
        bodyOffsetVec.transform(modelTranslateStack.last().pose());
        Vec3 offset = new Vec3(bodyOffsetVec.x(), bodyOffsetVec.y(), bodyOffsetVec.z());
        modelTranslateStack.popPose();
        return offset.add(0, -1.5F, 0);
    }


    public void setAlpha(float a){
        this.a = a;
    }

    public void renderToBuffer(PoseStack p_170506_, VertexConsumer p_170507_, int p_170508_, int p_170509_, float p_170510_, float p_170511_, float p_170512_, float p_170513_) {
        super.renderToBuffer(p_170506_, p_170507_, p_170508_, p_170509_, this.r * p_170510_, this.g * p_170511_, this.b * p_170512_, this.a * p_170513_);
    }


}
