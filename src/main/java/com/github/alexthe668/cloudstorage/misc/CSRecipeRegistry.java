package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSRecipeRegistry {

    public static final DeferredRegister<RecipeSerializer<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CloudStorage.MODID);
    public static final RegistryObject<RecipeSerializer<?>> BALLOON_ARROW_RECIPE = DEF_REG.register("balloon_arrow", () -> new SimpleCraftingRecipeSerializer<>(RecipeBalloonArrow::new));

}
