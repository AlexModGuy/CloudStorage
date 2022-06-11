package com.github.alexthe668.cloudstorage.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class CloudChestContainer extends SimpleContainer {

    public CloudChestContainer(int size) {
        super(size);
    }

    public void fromTag(ListTag tag) {
        for(int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundtag = tag.getCompound(i);
            int j = compoundtag.getInt("Slot");
            if (j >= 0 && j < getContainerSize()) {
                setItem(j, ItemStack.of(compoundtag));
            }
        }
    }

    public ListTag createTag() {
        ListTag listtag = new ListTag();

        for(int i = 0; i < getContainerSize(); ++i) {
            ItemStack itemstack = getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putInt("Slot", i);
                itemstack.save(compoundtag);
                listtag.add(compoundtag);
            }
        }
        return listtag;
    }
}
