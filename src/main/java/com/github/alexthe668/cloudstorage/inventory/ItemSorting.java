package com.github.alexthe668.cloudstorage.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemSorting {

    public static int defaultCompare(ItemStack stack1, ItemStack stack2) {
        if (stack1.sameItem(stack2)) {
            return 0;
        } else if (stack1.isEmpty()) {
            return 1;
        } else if (stack2.isEmpty()) {
            return -1;
        } else {
            ResourceLocation name1 = stack1.getItem().getRegistryName();
            ResourceLocation name2 = stack2.getItem().getRegistryName();
            int modid = name1.getNamespace().compareTo(name2.getNamespace());
            return Integer.compare(Item.getId(stack1.getItem()), Item.getId(stack2.getItem()));
        }
    }

    public static int addItem(Container container, ItemStack stack) {
        int i = getSlotWithRemainingSpace(container, stack);
        if (i == -1) {
            i = getFreeSlot(container);
        }

        return i == -1 ? stack.getCount() : addResource(container, i, stack);
    }

    public static int getSlotWithRemainingSpace(Container container, ItemStack p_36051_) {
        for(int i = 0; i < container.getContainerSize(); ++i) {
            if (hasRemainingSpaceForItem(container, container.getItem(i), p_36051_)) {
                return i;
            }
        }

        return -1;
    }

    public static int getFreeSlot(Container container) {
        for(int i = 0; i < container.getContainerSize(); ++i) {
            if (container.getItem(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private static int addResource(Container container, int p_36048_, ItemStack p_36049_) {
        Item item = p_36049_.getItem();
        int i = p_36049_.getCount();
        ItemStack itemstack = container.getItem(p_36048_);
        if (itemstack.isEmpty()) {
            itemstack = p_36049_.copy();
            itemstack.setCount(0);
            if (p_36049_.hasTag()) {
                itemstack.setTag(p_36049_.getTag().copy());
            }

            container.setItem(p_36048_, itemstack);
        }

        int j = i;
        if (i > itemstack.getMaxStackSize() - itemstack.getCount()) {
            j = itemstack.getMaxStackSize() - itemstack.getCount();
        }

        if (j > container.getMaxStackSize() - itemstack.getCount()) {
            j = container.getMaxStackSize() - itemstack.getCount();
        }

        if (j == 0) {
            return i;
        } else {
            i -= j;
            itemstack.grow(j);
            itemstack.setPopTime(5);
            return i;
        }
    }


    private static boolean hasRemainingSpaceForItem(Container container, ItemStack stack1, ItemStack stack2) {
        return !stack1.isEmpty() && ItemStack.isSameItemSameTags(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < container.getMaxStackSize();
    }

    public static NonNullList<ItemStack> combineStacks(Container container, NonNullList<ItemStack> itemList) {
        NonNullList<ItemStack> combined = NonNullList.create();
        for(ItemStack item : itemList){
            boolean merged = false;
            for(ItemStack already : combined){
                if(hasRemainingSpaceForItem(container, item, already)){
                    int combinedSize = item.getCount() + already.getCount();
                    if(combinedSize > item.getMaxStackSize()){
                        already.setCount(item.getMaxStackSize());
                        item.setCount(combinedSize - item.getMaxStackSize());
                    }else{
                        already.setCount(combinedSize);
                        merged = true;
                        break;
                    }
                }
            }
            if(!merged){
                combined.add(item);
            }
        }
        return combined;
    }
}
