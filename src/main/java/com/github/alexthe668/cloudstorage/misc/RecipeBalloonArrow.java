package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CommonProxy;
import com.github.alexthe668.cloudstorage.item.BalloonArrowItem;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;

public class RecipeBalloonArrow extends CustomRecipe {

    public RecipeBalloonArrow(ResourceLocation id) {
        super(id);
    }

    public boolean matches(CraftingContainer container, Level level) {
        ItemStack balloon = getBalloon(container);
        if(balloon.is(CSItemRegistry.BALLOON.get())){
            int arrows = 0;
            for(int i = 0; i < container.getContainerSize(); ++i) {
                ItemStack itemstack = container.getItem(i);
                if (itemstack.is(Items.ARROW)) {
                    arrows++;
                }
                if(arrows >= 8){
                    break;
                }
            }
            return arrows == 8;
        }
        return false;
    }

    public ItemStack assemble(CraftingContainer container) {
        ItemStack balloon = getBalloon(container);
        ItemStack balloonArrow = getResultItem();
        ((BalloonArrowItem)balloonArrow.getItem()).setColor(balloonArrow, BalloonItem.getBalloonColor(balloon));
        return balloonArrow;
    }

    private ItemStack getBalloon(CraftingContainer container) {
        for(int i = 0; i < container.getContainerSize(); ++i) {
            if(container.getItem(i).is(CSItemRegistry.BALLOON.get())){
                return container.getItem(i);
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getResultItem() {
        return new ItemStack(CSItemRegistry.BALLOON_ARROW.get(), 8);
    }

    public boolean canCraftInDimensions(int x, int y) {
        return x >= 3 && y >= 3;
    }

    public RecipeSerializer<?> getSerializer() {
        return CSRecipeRegistry.BALLOON_ARROW_RECIPE;
    }

    public boolean isSpecial() {
        return false;
    }

}
