package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class CSLootRegistry {
    public static final DeferredRegister<LootItemFunctionType> DEF_REG = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, CloudStorage.MODID);

    public static final RegistryObject<LootItemFunctionType> DYE_RANDOMLY = DEF_REG.register("dye_randomly", () -> new LootItemFunctionType(new DyeRandomlyLootFunction.Serializer()));

}
