package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe668.cloudstorage.client.ClientProxy;
import com.github.alexthe668.cloudstorage.client.model.PropellerHatModel;
import com.github.alexthe668.cloudstorage.entity.villager.CSVillagerRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;

public class VillagerHatLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    private PropellerHatModel hatModel;
    private static final ResourceLocation PROPELLER_HAT_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/propeller_hat.png");

    public VillagerHatLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
        Minecraft.getInstance().getModelManager();
        hatModel = new PropellerHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientProxy.PROPELLER_HAT_MODEL));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float f1, float f2, float f3, float f4, float f5, float f6) {
        if(entity instanceof Villager && ((Villager)entity).getVillagerData().getProfession() == CSVillagerRegistry.BALLOON_SALESMAN.get() || entity instanceof ZombieVillager && ((ZombieVillager)entity).getVillagerData().getProfession() == CSVillagerRegistry.BALLOON_SALESMAN.get()){
            if(!entity.isBaby()){
                poseStack.pushPose();
                this.getParentModel().getHead().translateAndRotate(poseStack);
                poseStack.translate(0.0F, -1.1F, 0.0F);
                poseStack.scale(1.35F, 1.35F, 1.35F);
                VertexConsumer ivertexbuilder = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(PROPELLER_HAT_TEXTURE));
                hatModel.withAnimations(entity).renderToBuffer(poseStack, ivertexbuilder, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            }
        }
    }
}
