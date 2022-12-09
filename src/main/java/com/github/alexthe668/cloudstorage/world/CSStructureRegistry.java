package com.github.alexthe668.cloudstorage.world;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSStructureRegistry {

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_DEF_REG = DeferredRegister.create(Registries.STRUCTURE_PIECE, CloudStorage.MODID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE_DEF_REG = DeferredRegister.create(Registries.STRUCTURE_TYPE, CloudStorage.MODID);

    public static final RegistryObject<StructureType<Structure>> SKY_TEMPLE_TYPE = STRUCTURE_TYPE_DEF_REG.register("sky_temple", () -> () -> SkyTempleStructure.TEMPLE_CODEC);
    public static final RegistryObject<StructureType<Structure>> BALLOON_TYPE = STRUCTURE_TYPE_DEF_REG.register("big_balloon", () -> () -> SkyTempleStructure.BALLOON_CODEC);
    public static final RegistryObject<StructurePieceType> SKY_TEMPLE_PIECE = STRUCTURE_PIECE_DEF_REG.register("sky_temple", () -> SkyTempleStructure.Piece::new);

    public static final ResourceKey<Structure> SKY_TEMPLE_KEY = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(CloudStorage.MODID, "sky_temple"));
    public static final ResourceKey<Structure> BIG_BALLOON_KEY = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(CloudStorage.MODID, "big_balloon"));
}
