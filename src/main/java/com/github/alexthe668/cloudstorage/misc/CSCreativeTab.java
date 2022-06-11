package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CSCreativeTab extends CreativeModeTab{

    public static final CSCreativeTab INSTANCE = new CSCreativeTab();

    private CSCreativeTab() {
        super(CloudStorage.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(CSItemRegistry.BALLOON.get());
    }
}