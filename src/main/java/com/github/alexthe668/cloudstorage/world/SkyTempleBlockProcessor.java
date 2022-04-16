package com.github.alexthe668.cloudstorage.world;

import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class SkyTempleBlockProcessor extends StructureProcessor {

    private WoodType woodType;
    private DyeColor woolColor1;
    private DyeColor woolColor2;

    private static final Map<DyeColor, Block> DYE_TO_BLOCK = Util.make(Maps.newEnumMap(DyeColor.class), (p_29841_) -> {
        p_29841_.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
        p_29841_.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
        p_29841_.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
        p_29841_.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        p_29841_.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
        p_29841_.put(DyeColor.LIME, Blocks.LIME_WOOL);
        p_29841_.put(DyeColor.PINK, Blocks.PINK_WOOL);
        p_29841_.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
        p_29841_.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        p_29841_.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
        p_29841_.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
        p_29841_.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
        p_29841_.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
        p_29841_.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
        p_29841_.put(DyeColor.RED, Blocks.RED_WOOL);
        p_29841_.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
    });


    public SkyTempleBlockProcessor(WoodType woodType, DyeColor woolColor1, DyeColor woolColor2) {
        this.woodType = woodType;
        this.woolColor1 = woolColor1;
        this.woolColor2 = woolColor2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }

    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos pos1, BlockPos pos2, StructureTemplate.StructureBlockInfo info1, StructureTemplate.StructureBlockInfo info2, StructurePlaceSettings settings) {
        Random random = settings.getRandom(info2.pos);
        if(info2.state.getBlock() == Blocks.WHITE_WOOL){
            return new StructureTemplate.StructureBlockInfo(info2.pos, DYE_TO_BLOCK.get(woolColor1).defaultBlockState(), info2.nbt);
        }else if(info2.state.getBlock() == Blocks.RED_WOOL){
            return new StructureTemplate.StructureBlockInfo(info2.pos, DYE_TO_BLOCK.get(woolColor2).defaultBlockState(), info2.nbt);
        }else if(info2.state.getBlock() instanceof SlabBlock && info2.state.getMaterial() == Material.WOOD){
            return new StructureTemplate.StructureBlockInfo(info2.pos, getWoodSlab(info2.state), info2.nbt);
        }else if(info2.state.getBlock() instanceof FenceBlock){
            return new StructureTemplate.StructureBlockInfo(info2.pos, getWoodFence(info2.state), info2.nbt);
        }else if(info2.state.getBlock() instanceof TrapDoorBlock){
            return new StructureTemplate.StructureBlockInfo(info2.pos, getWoodTrapdoor(info2.state), info2.nbt);
        }else if(info2.state.getBlock() == CSBlockRegistry.CLOUD.get() && random.nextFloat() < 0.1F){
            return new StructureTemplate.StructureBlockInfo(info2.pos, CSBlockRegistry.STATIC_CLOUD.get().defaultBlockState(), info2.nbt);
        }
        return info2;
    }

    private BlockState getWoodSlab(BlockState in){
        if(woodType == WoodType.OAK){
            return getNewStateWithProperties(in, Blocks.OAK_SLAB.defaultBlockState());
        }else if(woodType == WoodType.BIRCH){
            return getNewStateWithProperties(in, Blocks.BIRCH_SLAB.defaultBlockState());
        }else if(woodType == WoodType.SPRUCE){
            return getNewStateWithProperties(in, Blocks.SPRUCE_SLAB.defaultBlockState());
        }else if(woodType == WoodType.JUNGLE){
            return getNewStateWithProperties(in, Blocks.JUNGLE_SLAB.defaultBlockState());
        }else if(woodType == WoodType.DARK_OAK){
            return getNewStateWithProperties(in, Blocks.DARK_OAK_SLAB.defaultBlockState());
        }else if(woodType == WoodType.ACACIA){
            return getNewStateWithProperties(in, Blocks.ACACIA_SLAB.defaultBlockState());
        }
        return in;
    }

    private BlockState getWoodFence(BlockState in){
        if(woodType == WoodType.OAK){
            return getNewStateWithProperties(in, Blocks.OAK_FENCE.defaultBlockState());
        }else if(woodType == WoodType.BIRCH){
            return getNewStateWithProperties(in, Blocks.BIRCH_FENCE.defaultBlockState());
        }else if(woodType == WoodType.SPRUCE){
            return getNewStateWithProperties(in, Blocks.SPRUCE_FENCE.defaultBlockState());
        }else if(woodType == WoodType.JUNGLE){
            return getNewStateWithProperties(in, Blocks.JUNGLE_FENCE.defaultBlockState());
        }else if(woodType == WoodType.DARK_OAK){
            return getNewStateWithProperties(in, Blocks.DARK_OAK_FENCE.defaultBlockState());
        }else if(woodType == WoodType.ACACIA){
            return getNewStateWithProperties(in, Blocks.ACACIA_FENCE.defaultBlockState());
        }
        return in;
    }

    private BlockState getWoodTrapdoor(BlockState in){
        if(woodType == WoodType.OAK){
            return getNewStateWithProperties(in, Blocks.OAK_TRAPDOOR.defaultBlockState());
        }else if(woodType == WoodType.BIRCH){
            return getNewStateWithProperties(in, Blocks.BIRCH_TRAPDOOR.defaultBlockState());
        }else if(woodType == WoodType.SPRUCE){
            return getNewStateWithProperties(in, Blocks.SPRUCE_TRAPDOOR.defaultBlockState());
        }else if(woodType == WoodType.JUNGLE){
            return getNewStateWithProperties(in, Blocks.JUNGLE_TRAPDOOR.defaultBlockState());
        }else if(woodType == WoodType.DARK_OAK){
            return getNewStateWithProperties(in, Blocks.DARK_OAK_TRAPDOOR.defaultBlockState());
        }else if(woodType == WoodType.ACACIA){
            return getNewStateWithProperties(in, Blocks.ACACIA_TRAPDOOR.defaultBlockState());
        }
        return in;
    }

    private static BlockState getNewStateWithProperties(BlockState copied, BlockState blockstate) {
        for(Property property : copied.getProperties()) {
            blockstate = blockstate.hasProperty(property) ? blockstate.setValue(property, copied.getValue(property)) : blockstate;
        }
        return blockstate;
    }
}
