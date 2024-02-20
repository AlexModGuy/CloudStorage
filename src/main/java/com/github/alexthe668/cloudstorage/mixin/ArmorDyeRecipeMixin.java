package com.github.alexthe668.cloudstorage.mixin;

import com.github.alexthe668.cloudstorage.item.BalloonItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ArmorDyeRecipe.class)
public class ArmorDyeRecipeMixin {

    @Redirect(
            method = {"Lnet/minecraft/world/item/crafting/ArmorDyeRecipe;assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/DyeableLeatherItem;dyeArmor(Lnet/minecraft/world/item/ItemStack;Ljava/util/List;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack cs_dyeArmor(ItemStack stack, List<DyeItem> dyes) {
        if(BalloonItem.isLoot(stack)){
            return ItemStack.EMPTY;
        }else{
            return DyeableLeatherItem.dyeArmor(stack, dyes);
        }
    }
}
