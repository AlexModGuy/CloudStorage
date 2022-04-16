package com.github.alexthe668.cloudstorage.world;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSStructureRegistry {
    public static final DeferredRegister<StructureFeature<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, CloudStorage.MODID);
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> SKY_TEMPLE = DEF_REG.register("sky_temple", SkyTempleStructure::skyTemple);
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> BIG_BALLOON = DEF_REG.register("big_balloon", SkyTempleStructure::balloon);
}
