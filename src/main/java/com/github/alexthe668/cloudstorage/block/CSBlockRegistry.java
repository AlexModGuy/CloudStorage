package com.github.alexthe668.cloudstorage.block;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSBlockRegistry {
    public static final DeferredRegister<Block> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, CloudStorage.MODID);

    public static final RegistryObject<Block> CLOUD = DEF_REG.register("cloud", () -> new CloudBlock(false, BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.WOOL).friction(0.87F).sound(SoundType.WOOL).noOcclusion().isViewBlocking((state, level, pos) -> false).isSuffocating((state, level, pos) -> false)));
    public static final RegistryObject<Block> STATIC_CLOUD = DEF_REG.register("static_cloud", () -> new CloudBlock(true, BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.WOOL).friction(0.87F).sound(SoundType.WOOL).noOcclusion().isViewBlocking((state, level, pos) -> false).isSuffocating((state, level, pos) -> false)));
    public static final RegistryObject<Block> CLOUD_CHEST = DEF_REG.register("cloud_chest", () -> new CloudChestBlock(false));
    public static final RegistryObject<Block> STATIC_CLOUD_CHEST = DEF_REG.register("static_cloud_chest", () -> new CloudChestBlock(true));

}
