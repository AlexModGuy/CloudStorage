package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.item.CustomTabBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.C;

public class CSCreativeTab {

    public static final ResourceLocation TAB = new ResourceLocation("cloudstorage:cloudstorage");

    private static ItemStack makeIcon() {
        ItemStack stack = new ItemStack(CSItemRegistry.BALLOON.get());
        CompoundTag tag = new CompoundTag();
        tag.putInt("3DRender", 1);
        stack.setTag(tag);
        return stack;
    }

    public static void registerTab(CreativeModeTabEvent.Register event){
        event.registerCreativeModeTab(TAB, builder -> builder.title(Component.translatable("itemGroup.cloudstorage")).icon(CSCreativeTab::makeIcon).displayItems((flags, output, isOp) -> {
            for(RegistryObject<Item> item : CSItemRegistry.DEF_REG.getEntries()){
                if(item.get() instanceof CustomTabBehavior customTabBehavior){
                    customTabBehavior.fillItemCategory(output);
                }else{
                    output.accept(item.get());
                }
            }
        }));

    }
}