package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.item.CustomTabBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CSCreativeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> DEF_REG = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CloudStorage.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = DEF_REG.register(CloudStorage.MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + CloudStorage.MODID))
            .icon(() -> makeIcon())
            .displayItems((enabledFeatures, output) -> {
                for(RegistryObject<Item> item : CSItemRegistry.DEF_REG.getEntries()){
                    if(item.get() instanceof CustomTabBehavior customTabBehavior){
                        customTabBehavior.fillItemCategory(output);
                    }else{
                        output.accept(item.get());
                    }
                }
            })
            .build());

    private static ItemStack makeIcon() {
        ItemStack stack = new ItemStack(CSItemRegistry.BALLOON.get());
        CompoundTag tag = new CompoundTag();
        tag.putInt("3DRender", 1);
        stack.setTag(tag);
        return stack;
    }
}