package com.github.alexthe668.cloudstorage.block;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSBlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CloudStorage.MODID);
    public static final RegistryObject<BlockEntityType<CloudChestBlockEntity>> CLOUD_CHEST = DEF_REG.register("cloud_chest", () -> BlockEntityType.Builder.of(CloudChestBlockEntity::new, CSBlockRegistry.CLOUD_CHEST.get()).build(null));
    public static final RegistryObject<BlockEntityType<StaticCloudChestBlockEntity>> STATIC_CLOUD_CHEST = DEF_REG.register("static_cloud_chest", () -> BlockEntityType.Builder.of(StaticCloudChestBlockEntity::new, CSBlockRegistry.STATIC_CLOUD_CHEST.get()).build(null));

}
