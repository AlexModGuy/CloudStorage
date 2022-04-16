package com.github.alexthe668.cloudstorage.world;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.Locale;

public class CSStructureSetRegistry {

    public static final ResourceKey<StructureSet> SKY_TEMPLE = ResourceKey.create(Registry.STRUCTURE_SET_REGISTRY, new ResourceLocation("cloudstorage:sky_temple"));
    public static final ResourceKey<StructureSet> BIG_BALLOON = ResourceKey.create(Registry.STRUCTURE_SET_REGISTRY, new ResourceLocation("cloudstorage:big_balloon"));
    public static final Holder<StructureSet> SKY_TEMPLE_SET = register(SKY_TEMPLE, CSConfiguredStructureRegistry.CONFIGURED_SKY_TEMPLE_STRUCTURE, new RandomSpreadStructurePlacement(Math.max(CloudStorage.CONFIG.skyTempleMaxSeperation.get(), CloudStorage.CONFIG.skyTempleMinSeperation.get() + 1), CloudStorage.CONFIG.skyTempleMinSeperation.get(), RandomSpreadType.LINEAR, 21142069));
    public static final Holder<StructureSet> BIG_BALLOON_SET = register(BIG_BALLOON, CSConfiguredStructureRegistry.CONFIGURED_BIG_BALLOON_STRUCTURE, new RandomSpreadStructurePlacement(Math.max(CloudStorage.CONFIG.bigBalloonMaxSeperation.get(), CloudStorage.CONFIG.bigBalloonMinSeperation.get() + 1), CloudStorage.CONFIG.bigBalloonMinSeperation.get(), RandomSpreadType.LINEAR, 5932102));
    public static final StructurePieceType SKY_TEMPLE_PIECE = registerPiece(SkyTempleStructure.Piece::new, "SkyTemplePiece");

    public static void bootstrap() {
    }

    private static Holder<StructureSet> register(ResourceKey<StructureSet> setResourceKey, StructureSet structureSet) {
        return BuiltinRegistries.register(BuiltinRegistries.STRUCTURE_SETS, setResourceKey, structureSet);
    }

    private static StructurePieceType registerPiece(StructurePieceType structurePieceType, String name) {
        return Registry.register(Registry.STRUCTURE_PIECE, name.toLowerCase(Locale.ROOT), structurePieceType);
    }

    private static Holder<StructureSet> register(ResourceKey<StructureSet> setResourceKey, Holder<ConfiguredStructureFeature<?, ?>> configuredFeature, StructurePlacement structurePlacement) {
        return register(setResourceKey, new StructureSet(configuredFeature, structurePlacement));
    }
}