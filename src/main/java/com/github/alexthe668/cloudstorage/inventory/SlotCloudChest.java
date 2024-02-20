package com.github.alexthe668.cloudstorage.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotCloudChest extends Slot {

    public int slotId = 0;
    private boolean isGray;

    private CloudChestMenu menu;

    public SlotCloudChest(CloudChestMenu menu, Container container, int i, int i1, int i2) {
        super(container, i, i1, i2);
        this.slotId = i;
        this.menu = menu;
    }


    public int getScrollIndex(){
        return slotId + 9 * Math.max(getScrollAmount(), 0);
    }

    private int getScrollAmount() {
        return menu.getScrollAmount();
    }

    public ItemStack getItem() {
        return this.container.getItem(getScrollIndex());
    }

    @Override
    public void set(ItemStack stack) {
        if(getScrollIndex() < container.getContainerSize()) {
            this.container.setItem(this.getScrollIndex(), stack);
            this.setChanged();
        }
    }

    @Override
    public ItemStack remove(int count) {
        if(getScrollIndex() < container.getContainerSize()) {
            return this.container.removeItem(getScrollIndex(), count);
        }
        return ItemStack.EMPTY;
    }

    public int getSlotIndex() {
        return getScrollIndex();
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
