package com.github.alexthe668.cloudstorage.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class RenderFallingBlockWithTE  extends FallingBlockRenderer {

    public RenderFallingBlockWithTE(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(FallingBlockEntity cargo, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
        BlockState blockstate = cargo.getBlockState();
        Level level = cargo.getLevel();
        if (blockstate != level.getBlockState(cargo.blockPosition()) && blockstate.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED) {
            stack.pushPose();
            stack.translate(-0.5D, 0, -0.5D);
            if(blockstate.getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED && blockstate.hasProperty(HorizontalDirectionalBlock.FACING)){
                float f = blockstate.getValue(HorizontalDirectionalBlock.FACING).toYRot();
                stack.translate(0.5D, 0.5D, 0.5D);
                stack.mulPose(Vector3f.YP.rotationDegrees(-f));
                stack.translate(-0.5D, -0.5D, -0.5D);
            }
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockstate, stack, source, i, OverlayTexture.NO_OVERLAY);
            stack.popPose();
        }else{
            super.render(cargo, f1, f2, stack, source, i);
        }
    }
}
