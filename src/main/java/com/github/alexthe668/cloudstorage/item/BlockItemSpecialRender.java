package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class BlockItemSpecialRender extends CSBlockItem {

    public BlockItemSpecialRender(RegistryObject<Block> block, Properties props) {
        super(block, props);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept((net.minecraftforge.client.extensions.common.IClientItemExtensions) CloudStorage.PROXY.getISTERProperties(false));
    }
}
