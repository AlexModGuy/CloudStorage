package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CloudStorage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CSRecipeRegistry {
    public static SimpleRecipeSerializer BALLOON_ARROW_RECIPE;

    @SubscribeEvent
    public static void registerRecipes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
        BALLOON_ARROW_RECIPE = new SimpleRecipeSerializer<>(RecipeBalloonArrow::new);
        BALLOON_ARROW_RECIPE.setRegistryName(new ResourceLocation("cloudstorage:balloon_arrow"));
        event.getRegistry().register(BALLOON_ARROW_RECIPE);
    }
}
