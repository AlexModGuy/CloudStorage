package com.github.alexthe668.cloudstorage.world;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class CSStructureRegistry {

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_DEF_REG = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, CloudStorage.MODID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE_DEF_REG = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, CloudStorage.MODID);

    public static final RegistryObject<StructureType<SkyTempleStructure>> SKY_TEMPLE_TYPE = STRUCTURE_TYPE_DEF_REG.register("sky_temple", () -> () -> SkyTempleStructure.TEMPLE_CODEC);
    public static final RegistryObject<StructureType<SkyTempleStructure>> BALLOON_TYPE = STRUCTURE_TYPE_DEF_REG.register("big_balloon", () -> () -> SkyTempleStructure.BALLOON_CODEC);
    public static final RegistryObject<StructurePieceType> SKY_TEMPLE_PIECE = STRUCTURE_PIECE_DEF_REG.register("sky_temple", () -> SkyTempleStructure.Piece::new);

}
