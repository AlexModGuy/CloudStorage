package com.github.alexthe668.cloudstorage.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PropellerHatModel extends HumanoidModel {
    private final ModelPart propellerhat;
    private final ModelPart spinner;

    public PropellerHatModel(ModelPart root) {
        super(root);
        this.propellerhat = root.getChild("head").getChild("propeller_hat");
        this.spinner = propellerhat.getChild("spinner");
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");
        PartDefinition propellerhat = head.addOrReplaceChild("propeller_hat", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, deformation)
                .texOffs(0, 32).addBox(-1.0F, -13.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

        PartDefinition spinner = propellerhat.addOrReplaceChild("spinner", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public PropellerHatModel withAnimations(LivingEntity entity){
        float partialTick = Minecraft.getInstance().getFrameTime();
        float speed = entity.level().isThundering() ? 0.5F : entity.level().isRaining() ? 0.2F : 0.1F;
        float ageInTicks = (entity.tickCount + partialTick) * speed % ((float)Math.PI * 2F);
        this.spinner.yRot = ageInTicks;
        return this;
    }

}
