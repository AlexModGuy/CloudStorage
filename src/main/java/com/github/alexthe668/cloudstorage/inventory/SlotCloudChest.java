package com.github.alexthe668.cloudstorage.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class SlotCloudChest extends Slot {

    public int slot = 0;
    private boolean isGray;

    public SlotCloudChest(Container container, int i, int i1, int i2) {
        super(container, i, i1, i2);
        this.slot = i;
    }

    public abstract int getScrollIndex();

    public ItemStack getItem() {
        if(getScrollIndex() < container.getContainerSize()) {
            return this.container.getItem(getScrollIndex());
        }else{
            return ItemStack.EMPTY;
        }
    }

    public void set(ItemStack stack) {
        if(getScrollIndex() < container.getContainerSize()) {
            this.container.setItem(this.getScrollIndex(), stack);
            this.setChanged();
        }
    }

    public ItemStack remove(int count) {
        if(getScrollIndex() < container.getContainerSize()) {
            return this.container.removeItem(getScrollIndex(), count);
        }
        return ItemStack.EMPTY;
    }

    public int getSlotIndex() {
        return slot;
    }

    public int getContainerSlot() {
        return getScrollIndex();
    }

    public boolean isGray() {
        return isGray;
    }

    public void setGray(boolean isGray){
        this.isGray = isGray;
    }
}
