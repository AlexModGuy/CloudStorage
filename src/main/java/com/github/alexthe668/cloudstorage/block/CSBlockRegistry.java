package com.github.alexthe668.cloudstorage.block;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.item.BlockItemSpecialRender;
import com.github.alexthe668.cloudstorage.item.CSBlockItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.misc.CSCreativeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CSBlockRegistry {
    public static final DeferredRegister<Block> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, CloudStorage.MODID);

    public static final RegistryObject<Block> CLOUD = registerBlockAndItem("cloud", () -> new CloudBlock(false, BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.WOOL).friction(0.87F).sound(SoundType.WOOL).noOcclusion().isViewBlocking((state, level, pos) -> false).isSuffocating((state, level, pos) -> false)));
    public static final RegistryObject<Block> STATIC_CLOUD = registerBlockAndItem("static_cloud", () -> new CloudBlock(true, BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.WOOL).friction(0.87F).sound(SoundType.WOOL).noOcclusion().isViewBlocking((state, level, pos) -> false).isSuffocating((state, level, pos) -> false)));
    public static final RegistryObject<Block> CLOUD_CHEST = registerBlockAndItem("cloud_chest", () -> new CloudChestBlock(false));
    public static final RegistryObject<Block> STATIC_CLOUD_CHEST = registerBlockAndItem("static_cloud_chest", () -> new CloudChestBlock(true));
    public static final RegistryObject<Block> BALLOON_STAND = registerBlockAndItem("balloon_stand", () -> new BalloonStandBlock());

    public static RegistryObject<Block> registerBlockAndItem(String name, Supplier<Block> block){
        RegistryObject<Block> blockObj = DEF_REG.register(name, block);
        CSItemRegistry.DEF_REG.register(name, () -> name.equals("cloud_chest") || name.equals("static_cloud_chest") ? new BlockItemSpecialRender(blockObj, new Item.Properties().tab(CSCreativeTab.INSTANCE)) : new CSBlockItem(blockObj, new Item.Properties().tab(CSCreativeTab.INSTANCE)));
        return blockObj;
    }
}
