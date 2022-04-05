package com.github.alexthe668.cloudstorage.inventory;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;

@Mod.EventBusSubscriber(modid = CloudStorage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CSMenuRegistry {

    public static final MenuType<CloudChestMenu> CLOUD_CHEST_MENU = (MenuType<CloudChestMenu>) new MenuType<CloudChestMenu>(CloudChestMenu::new).setRegistryName("cloudstorage:cloud_chest_menu");
    public static final MenuType<BalloonStandMenu> BALLOON_STAND_MENU = (MenuType<BalloonStandMenu>) new MenuType<BalloonStandMenu>(BalloonStandMenu::new).setRegistryName("cloudstorage:balloon_stand_menu");

    @SubscribeEvent
    public static void registerMenuTypes(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().registerAll(CLOUD_CHEST_MENU, BALLOON_STAND_MENU);
    }
}
