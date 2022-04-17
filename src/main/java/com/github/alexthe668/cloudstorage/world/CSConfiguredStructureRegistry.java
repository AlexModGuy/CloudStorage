package com.github.alexthe668.cloudstorage.world;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class CSConfiguredStructureRegistry {

    public static final TagKey<Biome> HAS_BIG_BALLOONS = create("has_big_balloons");

    public static final ResourceKey<ConfiguredStructureFeature<?, ?>> CONFIGURED_SKY_TEMPLE = ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation("cloudstorage:sky_temple"));
    public static final ResourceKey<ConfiguredStructureFeature<?, ?>> CONFIGURED_BIG_BALLOON = ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation("cloudstorage:big_balloon"));
    public static final Holder<ConfiguredStructureFeature<?, ?>> CONFIGURED_SKY_TEMPLE_STRUCTURE = register(CONFIGURED_SKY_TEMPLE, CSStructureRegistry.SKY_TEMPLE.get().configured(NoneFeatureConfiguration.INSTANCE, HAS_BIG_BALLOONS));
    public static final Holder<ConfiguredStructureFeature<?, ?>> CONFIGURED_BIG_BALLOON_STRUCTURE = register(CONFIGURED_BIG_BALLOON, CSStructureRegistry.BIG_BALLOON.get().configured(NoneFeatureConfiguration.INSTANCE, HAS_BIG_BALLOONS));


    private static TagKey<Biome> create(String str) {
        return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(CloudStorage.MODID, str));
    }

    private static <FC extends FeatureConfiguration, F extends StructureFeature<FC>> Holder<ConfiguredStructureFeature<?, ?>> register(ResourceKey<ConfiguredStructureFeature<?, ?>> resourceKey, ConfiguredStructureFeature<FC, F> configuredStructureFeature) {
        return BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, resourceKey, configuredStructureFeature);
    }

}
