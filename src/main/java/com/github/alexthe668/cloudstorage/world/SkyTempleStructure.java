package com.github.alexthe668.cloudstorage.world;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.block.CloudChestBlockEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonTieEntity;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.Random;

public class SkyTempleStructure extends StructureFeature<NoneFeatureConfiguration> {

    private static final ResourceLocation[] TEMPLES = new ResourceLocation[]{
            new ResourceLocation("cloudstorage:sky_temple_0"),
            new ResourceLocation("cloudstorage:sky_temple_1"),
            new ResourceLocation("cloudstorage:sky_temple_2")
    };

    private static final ResourceLocation[] BALLOONS = new ResourceLocation[]{
            new ResourceLocation("cloudstorage:big_balloon_0"),
            new ResourceLocation("cloudstorage:big_balloon_1"),
            new ResourceLocation("cloudstorage:big_balloon_2")
    };

    public static SkyTempleStructure balloon() {
        return new SkyTempleStructure(SkyTempleStructure::generateBalloonPieces);
    }

    public static SkyTempleStructure skyTemple() {
        return new SkyTempleStructure(SkyTempleStructure::generateTemplePieces);
    }

    private SkyTempleStructure(PieceGenerator supplier) {
        super(NoneFeatureConfiguration.CODEC, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG), supplier));
    }

    private static void generateTemplePieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> contex) {
        if(CloudStorage.CONFIG.generateSkyTemples.get()){
            Rotation rotation = Rotation.getRandom(contex.random());
            LevelHeightAccessor levelHeight = contex.heightAccessor();
            int y = contex.chunkGenerator().getFirstOccupiedHeight(contex.chunkPos().getMinBlockX(), contex.chunkPos().getMinBlockZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeight) + 3;
            int randomHeight = 10 + contex.random().nextInt(10);
            BlockPos blockpos = new BlockPos(contex.chunkPos().getMinBlockX(), Math.max(y, CloudStorage.CONFIG.cloudHeight.get()) + randomHeight, contex.chunkPos().getMinBlockZ());
            ResourceLocation res = Util.getRandom(TEMPLES, contex.random());
            builder.addPiece(new Piece(contex.structureManager(), res, blockpos, rotation, contex.random().nextLong()));
        }
    }

    private static void generateBalloonPieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> contex) {
        if(CloudStorage.CONFIG.generateBigBalloons.get()) {
            Rotation rotation = Rotation.getRandom(contex.random());
            LevelHeightAccessor levelHeight = contex.heightAccessor();
            int y = contex.chunkGenerator().getFirstOccupiedHeight(contex.chunkPos().getMinBlockX(), contex.chunkPos().getMinBlockZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeight) + 3;
            int randomHeight = 10 + contex.random().nextInt(40);
            BlockPos blockpos = new BlockPos(contex.chunkPos().getMinBlockX(), Math.max(y, CloudStorage.CONFIG.cloudHeight.get()) + randomHeight, contex.chunkPos().getMinBlockZ());
            ResourceLocation res = Util.getRandom(BALLOONS, contex.random());
            builder.addPiece(new Piece(contex.structureManager(), res, blockpos, rotation, contex.random().nextLong()));
        }
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.STRONGHOLDS;
    }

    public static class Piece extends TemplateStructurePiece {
        private long seed;

        public Piece(StructureManager manager, ResourceLocation resourceLocation, BlockPos pos, Rotation rotation, long seed) {
            super(CSStructureSetRegistry.SKY_TEMPLE_PIECE, 0, manager, resourceLocation, resourceLocation.toString(), makeSettings(rotation, seed), pos);
            this.seed = seed;
        }

        public Piece(StructureManager manager, CompoundTag tag) {
            super(CSStructureSetRegistry.SKY_TEMPLE_PIECE, tag, manager, (x) -> {
                return makeSettings(Rotation.valueOf(tag.getString("Rotation")), tag.getLong("Seed"));
            });
        }

        public Piece(StructurePieceSerializationContext context, CompoundTag tag) {
            this(context.structureManager(), tag);
        }

        private static StructurePlaceSettings makeSettings(Rotation rotation, long seed) {
            StructurePlaceSettings settings = (new StructurePlaceSettings()).setRotation(rotation).setMirror(Mirror.NONE);
            settings.clearProcessors();
            settings.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
            Random random = new Random(seed);
            WoodType woodType;
            switch (random.nextInt(5)) {
                case 1:
                    woodType = WoodType.BIRCH;
                    break;
                case 2:
                    woodType = WoodType.SPRUCE;
                    break;
                case 3:
                    woodType = WoodType.JUNGLE;
                    break;
                case 4:
                    woodType = WoodType.ACACIA;
                    break;
                case 5:
                    woodType = WoodType.DARK_OAK;
                    break;
                default:
                    woodType = WoodType.OAK;
            }
            DyeColor dyecolor1 = Util.getRandom(DyeColor.values(), random);
            DyeColor dyecolor2 = Util.getRandom(DyeColor.values(), random);
            settings.addProcessor(new SkyTempleBlockProcessor(woodType, dyecolor1, dyecolor2));
            return settings;
        }

        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            super.addAdditionalSaveData(context, tag);
            tag.putString("Rotation", this.placeSettings.getRotation().name());
            tag.putLong("Seed", this.seed);
        }

        protected void handleDataMarker(String string, BlockPos pos, ServerLevelAccessor accessor, Random random, BoundingBox box) {
            accessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 1);
            switch (string) {
                case "cloud_chest":
                    accessor.setBlock(pos, CSBlockRegistry.CLOUD_CHEST.get().defaultBlockState().rotate(placeSettings.getRotation()), 1);
                    if (accessor.getBlockEntity(pos) instanceof CloudChestBlockEntity cloudChest) {
                        cloudChest.setLootBalloon(random.nextInt(0xFFFFFF), BalloonItem.LOOT_TABLE, seed);
                    }
                    break;
                case "bloviator_spawner":
                    accessor.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 0);
                    if (accessor.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
                        spawner.getSpawner().setEntityId(CSEntityRegistry.BLOVIATOR.get());
                    }
                    break;
                case "badloon_spawner":
                    accessor.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 0);
                    if (accessor.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
                        spawner.getSpawner().setEntityId(CSEntityRegistry.BADLOON.get());
                    }
                    break;
                case "observer_up":
                    accessor.setBlock(pos, Blocks.OBSERVER.defaultBlockState().setValue(ObserverBlock.FACING, Direction.UP), 0);
                    break;
                case "observer_south":
                    accessor.setBlock(pos, Blocks.OBSERVER.defaultBlockState().setValue(ObserverBlock.FACING, Direction.SOUTH).rotate(placeSettings.getRotation()), 0);
                    break;
                case "tied_balloon":
                    Random seedRng = new Random(seed);
                    switch (seedRng.nextInt(5)){
                        case 1:
                            accessor.setBlock(pos, Blocks.BIRCH_FENCE.defaultBlockState(), 2);
                            break;
                        case 2:
                            accessor.setBlock(pos, Blocks.SPRUCE_FENCE.defaultBlockState(), 2);
                            break;
                        case 3:
                            accessor.setBlock(pos, Blocks.JUNGLE_FENCE.defaultBlockState(), 2);
                            break;
                        case 4:
                            accessor.setBlock(pos, Blocks.ACACIA_FENCE.defaultBlockState(), 2);
                            break;
                        case 5:
                            accessor.setBlock(pos, Blocks.DARK_OAK_FENCE.defaultBlockState(), 2);
                            break;
                        default:
                            accessor.setBlock(pos, Blocks.OAK_FENCE.defaultBlockState(), 2);
                    }
                    if (!accessor.getBlockState(pos).isAir()) {
                        BalloonTieEntity tie = new BalloonTieEntity(accessor.getLevel(), pos);
                        tie.setBalloonCount(1);
                        accessor.addFreshEntityWithPassengers(tie);
                        BalloonEntity balloon = CSEntityRegistry.BALLOON.get().create(accessor.getLevel());
                        balloon.setStringLength(3);
                        balloon.setPos(pos.getX() + 0.5F, pos.getY() + 3.1F, pos.getZ() + 0.5F);
                        balloon.setChildId(tie.getUUID());
                        accessor.addFreshEntityWithPassengers(balloon);
                    }
                    break;
            }
        }
    }

}
