package com.github.alexthe668.cloudstorage.client.model.baked;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BakedModelFinalLayerFullbright extends BakedModelWrapper {

    public BakedModelFinalLayerFullbright(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, @Nonnull IModelData extraData){
        if (state == null) {
            return originalModel.getQuads(state, side, rand, extraData);
        }
        return transformLastQuad(originalModel.getQuads(state, side, rand, extraData));
    }

    private static List<BakedQuad> transformLastQuad(List<BakedQuad> oldQuads) {
        List<BakedQuad> quads = new ArrayList<>(oldQuads);
        if(!quads.isEmpty()){
            BakedQuad quad = quads.get(quads.size() - 1);
            quads.set(quads.size() - 1, setFullbright(quad));

        }
        return quads;
    }

    private static BakedQuad setFullbright(BakedQuad quad) {
        int[] vertexData = quad.getVertices().clone();
        int step = vertexData.length / 4;

        vertexData[6] = 0x00F000F0;
        vertexData[6 + step] = 0x00F000F0;
        vertexData[6 + 2 * step] = 0x00F000F0;
        vertexData[6 + 3 * step] = 0x00F000F0;
        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }
}
