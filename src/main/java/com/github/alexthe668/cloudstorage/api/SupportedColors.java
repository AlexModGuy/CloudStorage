package com.github.alexthe668.cloudstorage.api;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SupportedColors {

    private static final EnumMap<DyeColor, Supplier<? extends Block>> WOOLS = new EnumMap<>(DyeColor.class);

    public static void register(DyeColor color, Supplier<? extends Block> blockSupplier) {
        WOOLS.put(color, blockSupplier);
    }

    public static void registerVanillaColors() {
        register(DyeColor.WHITE, () -> Blocks.WHITE_WOOL);
        register(DyeColor.ORANGE, () -> Blocks.ORANGE_WOOL);
        register(DyeColor.MAGENTA, () -> Blocks.MAGENTA_WOOL);
        register(DyeColor.LIGHT_BLUE, () -> Blocks.LIGHT_BLUE_WOOL);
        register(DyeColor.YELLOW, () -> Blocks.YELLOW_WOOL);
        register(DyeColor.LIME, () -> Blocks.LIME_WOOL);
        register(DyeColor.PINK, () -> Blocks.PINK_WOOL);
        register(DyeColor.GRAY, () -> Blocks.GRAY_WOOL);
        register(DyeColor.LIGHT_GRAY, () -> Blocks.LIGHT_GRAY_WOOL);
        register(DyeColor.CYAN, () -> Blocks.CYAN_WOOL);
        register(DyeColor.PURPLE, () -> Blocks.PURPLE_WOOL);
        register(DyeColor.BLUE, () -> Blocks.BLUE_WOOL);
        register(DyeColor.BROWN, () -> Blocks.BROWN_WOOL);
        register(DyeColor.GREEN, () -> Blocks.GREEN_WOOL);
        register(DyeColor.RED, () -> Blocks.RED_WOOL);
        register(DyeColor.BLACK, () -> Blocks.BLACK_WOOL);
    }

    public static Optional<Block> getWool(DyeColor color) {
        return Optional.ofNullable(WOOLS.get(color)).map(Supplier::get);
    }

    public static Stream<DyeColor> supported() {
        return WOOLS.keySet().stream();
    }

}
