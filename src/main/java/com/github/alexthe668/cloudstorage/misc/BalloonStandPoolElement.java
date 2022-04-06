package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonTieEntity;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.entity.villager.CSVillagerRegistry;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.structures.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.Random;
import java.util.function.Supplier;

public class BalloonStandPoolElement extends LegacySinglePoolElement {

    public static final Codec<BalloonStandPoolElement> CODEC = RecordCodecBuilder.create((p_210357_) -> {
        return p_210357_.group(templateCodec(), processorsCodec(), projectionCodec()).apply(p_210357_, BalloonStandPoolElement::new);
    });

    protected BalloonStandPoolElement(Either<ResourceLocation, StructureTemplate> either, Supplier<StructureProcessorList> p_210349_, StructureTemplatePool.Projection p_210350_) {
        super(either, p_210349_, p_210350_);
    }

    public BalloonStandPoolElement(ResourceLocation resourceLocation, Supplier<StructureProcessorList> processors) {
        super(Either.left(resourceLocation), processors, StructureTemplatePool.Projection.RIGID);
    }

    @Override
    public void handleDataMarker(LevelAccessor accessor, StructureTemplate.StructureBlockInfo structureBlockInfo, BlockPos pos, Rotation rotation, Random random, BoundingBox box) {
        String contents = structureBlockInfo.nbt.getString("metadata");
        if (contents.startsWith("balloons")) {
            int secondary = 0;
            try {
                secondary = Integer.parseInt(contents.substring(8));
            } catch (Exception e) {
                CloudStorage.LOGGER.warn("could not parse balloon NBT");
            }
            if (accessor instanceof ServerLevelAccessor serverLevel && !accessor.getBlockState(structureBlockInfo.pos).isAir()) {
                BalloonTieEntity tie = new BalloonTieEntity(serverLevel.getLevel(), structureBlockInfo.pos.below());
                if (accessor.getBlockState(structureBlockInfo.pos.below()).getBlock() == CSBlockRegistry.BALLOON_STAND.get()) {
                    tie.setPos(tie.getX(), tie.getY() + 0.3F, tie.getZ());
                }
                int balloons = 1 + random.nextInt(3);
                tie.setBalloonCount(balloons);
                ((ServerLevelAccessor) accessor).addFreshEntityWithPassengers(tie);
                int[] colors = getBalloonColors(secondary);
                for (int i = 0; i < balloons; i++) {
                    BalloonEntity balloon = CSEntityRegistry.BALLOON.get().create(serverLevel.getLevel());
                    balloon.setPos(structureBlockInfo.pos.getX() + 0.5F, structureBlockInfo.pos.getY() + 0.1F, structureBlockInfo.pos.getZ() + 0.5F);
                    balloon.setStringLength(BalloonEntity.DEFAULT_STRING_LENGTH + random.nextInt(2));
                    balloon.setBalloonColor(colors[Mth.clamp(random.nextInt(colors.length), 0, colors.length - 1)]);
                    balloon.setChildId(tie.getUUID());
                    ((ServerLevelAccessor) accessor).addFreshEntityWithPassengers(balloon);
                }
            }
            accessor.setBlock(structureBlockInfo.pos, Blocks.AIR.defaultBlockState(), 2);
        }
    }

    private int[] getBalloonColors(int index) {
        switch (index) {
            case 1:
                return CSVillagerRegistry.getBalloonColorsForVillager(VillagerType.PLAINS);
            case 2:
                return CSVillagerRegistry.getBalloonColorsForVillager(VillagerType.DESERT);
            case 3:
                return CSVillagerRegistry.getBalloonColorsForVillager(VillagerType.SAVANNA);
            case 4:
                return CSVillagerRegistry.getBalloonColorsForVillager(VillagerType.SNOW);
            case 5:
                return CSVillagerRegistry.getBalloonColorsForVillager(VillagerType.TAIGA);
        }
        return new int[]{BalloonItem.DEFAULT_COLOR};
    }

    @Override
    public boolean place(StructureManager p_210435_, WorldGenLevel p_210436_, StructureFeatureManager p_210437_, ChunkGenerator p_210438_, BlockPos p_210439_, BlockPos p_210440_, Rotation p_210441_, BoundingBox p_210442_, Random p_210443_, boolean p_210444_) {
        StructureTemplate structuretemplate = p_210435_.getOrCreate(template.left().get());
        StructurePlaceSettings structureplacesettings = this.getSettings(p_210441_, p_210442_, p_210444_);
        if (!structuretemplate.placeInWorld(p_210436_, p_210439_, p_210440_, structureplacesettings, p_210443_, 18)) {
            return false;
        } else {
            for (StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : StructureTemplate.processBlockInfos(p_210436_, p_210439_, p_210440_, structureplacesettings, this.getDataMarkers(p_210435_, p_210439_, p_210441_, false), structuretemplate)) {
                this.handleDataMarker(p_210436_, structuretemplate$structureblockinfo, p_210439_, p_210441_, p_210443_, p_210442_);
            }
            return true;
        }
    }

    @Override
    protected StructurePlaceSettings getSettings(Rotation p_210421_, BoundingBox p_210422_, boolean p_210423_) {
        StructurePlaceSettings structureplacesettings = new StructurePlaceSettings();
        structureplacesettings.setBoundingBox(p_210422_);
        structureplacesettings.setRotation(p_210421_);
        structureplacesettings.setKnownShape(true);
        structureplacesettings.setIgnoreEntities(false);
        structureplacesettings.setFinalizeEntities(true);
        if (!p_210423_) {
            structureplacesettings.addProcessor(JigsawReplacementProcessor.INSTANCE);
        }
        this.processors.get().list().forEach(structureplacesettings::addProcessor);
        this.getProjection().getProcessors().forEach(structureplacesettings::addProcessor);
        return structureplacesettings;
    }

    public StructurePoolElementType<?> getType() {
        return CSVillagerRegistry.BALLOON_STAND_TYPE;
    }

    public String toString() {
        return "BalloonStand[" + this.template + "]";
    }
}
