package com.github.alexthe668.cloudstorage.inventory;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSMenuRegistry {

    public static final DeferredRegister<MenuType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CloudStorage.MODID);

    public static final RegistryObject<MenuType<CloudChestMenu>> CLOUD_CHEST_MENU = DEF_REG.register("cloud_chest_menu", () -> new MenuType<CloudChestMenu>(CloudChestMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<MenuType<BalloonStandMenu>> BALLOON_STAND_MENU = DEF_REG.register("balloon_stand_menu", () -> new MenuType<BalloonStandMenu>(BalloonStandMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
