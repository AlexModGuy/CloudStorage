package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.misc.CSCreativeTab;
import com.github.alexthe668.cloudstorage.misc.CSSoundRegistry;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSItemRegistry {
    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, CloudStorage.MODID);
    public static final RegistryObject<Item> SPAWN_EGG_BADLOON = DEF_REG.register("spawn_egg_badloon", () -> new ForgeSpawnEggItem(CSEntityRegistry.BADLOON, 0XE72929, 0XEEE7E1, new Item.Properties().tab(CSCreativeTab.INSTANCE)));
    public static final RegistryObject<Item> SPAWN_EGG_BLOVIATOR = DEF_REG.register("spawn_egg_bloviator", () -> new ForgeSpawnEggItem(CSEntityRegistry.BLOVIATOR, 0XDFF3F7, 0X24AFFF, new Item.Properties().tab(CSCreativeTab.INSTANCE)));
    public static final RegistryObject<Item> BALLOON_BIT = DEF_REG.register("balloon_bit", () -> new Item(new Item.Properties().tab(CSCreativeTab.INSTANCE)));
    public static final RegistryObject<Item> GUIDE_BOOK = DEF_REG.register("guide_book", () -> new GuideBookItem(new Item.Properties().tab(CSCreativeTab.INSTANCE).stacksTo(1)));
    public static final RegistryObject<Item> BALLOON_INVENTORY = DEF_REG.register("balloon_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BALLOON = DEF_REG.register("balloon", () -> new BalloonItem());
    public static final RegistryObject<Item> ANGRY_CLOUD_IN_A_BOTTLE = DEF_REG.register("angry_cloud_in_a_bottle", () -> new CloudBottleItem(false));
    public static final RegistryObject<Item> HAPPY_CLOUD_IN_A_BOTTLE = DEF_REG.register("happy_cloud_in_a_bottle", () -> new CloudBottleItem(true));
    public static final RegistryObject<Item> BALLOON_BUDDY_INVENTORY = DEF_REG.register("balloon_buddy_inventory", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BALLOON_BUDDY = DEF_REG.register("balloon_buddy", () -> new BalloonBuddyItem());
    public static final RegistryObject<Item> BALLOON_ARROW = DEF_REG.register("balloon_arrow", () -> new BalloonArrowItem());
    public static final RegistryObject<Item> PROPELLER_HAT = DEF_REG.register("propeller_hat", () -> new PropellerHatItem());
    public static final RegistryObject<Item> COTTON_CANDY = DEF_REG.register("cotton_candy", () -> new Item(new Item.Properties().tab(CSCreativeTab.INSTANCE).food((new FoodProperties.Builder()).nutrition(4).saturationMod(0.15F).fast().build())));
    public static final RegistryObject<Item> MUSIC_DISC_DRIFT = DEF_REG.register("music_disc_drift", () -> new RecordItem(14, CSSoundRegistry.MUSIC_DISC_DRIFT, new Item.Properties().tab(CSCreativeTab.INSTANCE).stacksTo(1).rarity(Rarity.RARE)));
}
