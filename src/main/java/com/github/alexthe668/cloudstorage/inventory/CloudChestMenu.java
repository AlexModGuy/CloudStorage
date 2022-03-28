package com.github.alexthe668.cloudstorage.inventory;

import com.github.alexthe668.cloudstorage.CloudStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.*;

public class CloudChestMenu extends AbstractContainerMenu {
    public static int size = -1;
    public static int scrollAmount = -1;
    public String currentSearch = "";
    private final Container container;
    private Map<ItemStack, Integer> restoreFromAfterSearch = new HashMap<>();

    public CloudChestMenu(int id, Inventory playerInv) {
        this(id, playerInv, new SimpleContainer(size == -1 ? 1 : size));
        size = -1;
    }

    public CloudChestMenu(int id, Inventory playerInv, Container containerIn) {
        super(CSMenuRegistry.CLOUD_CHEST_MENU, id);
        this.container = containerIn;
        containerIn.startOpen(playerInv.player);
        int clampedSize = Math.min(containerIn.getContainerSize(), 54);

        int ySlots = clampedSize / 9;
        for (int k = 0; k < ySlots; ++k) {
            for (int l = 0; l < 9 && l + k * 9 < clampedSize; ++l) {
                this.addSlot(new SlotCloudChest(this.container, l + k * 9, 8 + l * 18, 18 + k * 18));
            }
        }
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(playerInv, k1 + i1 * 9 + 9, 8 + k1 * 18, 140 + i1 * 18));
            }
        }

        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(playerInv, j1, 8 + j1 * 18, 198));
        }
        if (size == -1) {
            CloudStorage.PROXY.setVisibleCloudSlots(containerIn.getContainerSize());
        }
        size = containerIn.getContainerSize();
        scrollTo(0.0F);
    }

    public static boolean matchesSearch(Player player, String search, ItemStack stack) {
        boolean matches = false;
        for (Component line : stack.getTooltipLines(player, TooltipFlag.Default.ADVANCED)) {
            if (ChatFormatting.stripFormatting(line.getString()).toLowerCase(Locale.ROOT).contains(search)) {
                matches = true;
                break;
            }
        }
        if (search.startsWith("@")) {
            String modid = search.substring(1);
            matches = stack.getItem().getRegistryName().getNamespace().contains(modid);
        } else if (search.startsWith("#")) {
            String tagId = search.substring(1);
            List<TagKey<Item>> tags = stack.getTags().toList();
            for(TagKey<Item> tag : tags){
                ResourceLocation registryName = tag.location();
                if(registryName.toString().contains(tagId)){
                    matches = true;
                    break;
                }
            }
        }
        return matches;
    }

    public void scrollTo(float scrollProgress) {
        int clampedSize = Math.min(container.getContainerSize(), 54);
        for (int i = 0; i < clampedSize; i++) {
            if (getSlot(i) instanceof SlotCloudChest slotCloudChest) {
                int maxScrollDown = CloudStorage.PROXY.getVisibleCloudSlots() / 9 - 6;
                if (maxScrollDown > 0) {
                    int j = (int) ((double) (scrollProgress * (float) maxScrollDown));
                    scrollAmount = j;
                } else {
                    scrollAmount = 0;
                }
            }
        }
    }

    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotIndex < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackToScrollable(itemstack1, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    protected boolean moveItemStackToScrollable(ItemStack beingMoved, int startAt, int endAt, boolean moveBackwards) {
        boolean flag = false;
        int i = startAt;
        if (moveBackwards) {
            i = endAt - 1;
        }

        if (beingMoved.isStackable()) {
            while (!beingMoved.isEmpty()) {
                if (moveBackwards) {
                    if (i < startAt) {
                        break;
                    }
                } else if (i >= endAt) {
                    break;
                }

                ItemStack itemstack = this.container.getItem(i);
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(beingMoved, itemstack)) {
                    int j = itemstack.getCount() + beingMoved.getCount();
                    int maxSize = Math.min(itemstack.getMaxStackSize(), beingMoved.getMaxStackSize());
                    if (j <= maxSize) {
                        beingMoved.setCount(0);
                        itemstack.setCount(j);
                        container.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        beingMoved.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        container.setChanged();
                        flag = true;
                    }
                }

                if (moveBackwards) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!beingMoved.isEmpty()) {
            if (moveBackwards) {
                i = endAt - 1;
            } else {
                i = startAt;
            }

            while (true) {
                if (moveBackwards) {
                    if (i < startAt) {
                        break;
                    }
                } else if (i >= endAt) {
                    break;
                }

                ItemStack itemstack1 = this.container.getItem(i);
                if (itemstack1.isEmpty()) {
                    if (beingMoved.getCount() > itemstack1.getMaxStackSize()) {
                        container.setItem(i, beingMoved.split(itemstack1.getMaxStackSize()));
                    } else {
                        container.setItem(i, beingMoved.split(beingMoved.getCount()));
                    }
                    container.setChanged();
                    flag = true;
                    break;
                }

                if (moveBackwards) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    public void updateGrays(Player player, String currentSearch){
        for (Slot slot : this.slots){
            if(slot instanceof SlotCloudChest cloudChest){
                cloudChest.setGray(!currentSearch.isEmpty() && !matchesSearch(player, currentSearch, cloudChest.getItem()));
            }
        }
    }

    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public void sort(Comparator<ItemStack> comparator) {
        NonNullList<ItemStack> itemList = NonNullList.create();

        for (int i = 0; i < this.container.getContainerSize(); i++) {
            if (!container.getItem(i).isEmpty()) {
                itemList.add(container.getItem(i));
            }
        }
        itemList.sort(comparator);
        itemList = ItemSorting.combineStacks(container, itemList);
        container.clearContent();
        for (int j = 0; j < container.getContainerSize() && j < itemList.size(); j++) {
            container.setItem(j, itemList.get(j));
        }
        container.setChanged();
        broadcastChanges();
    }

    public void search(Player player, String search) {
        if(search.isEmpty()){
            if(!restoreFromAfterSearch.isEmpty()){
                List<ItemStack> extras = new ArrayList<>();
                for (int i = 0; i < this.container.getContainerSize(); i++) {
                    ItemStack stack = container.getItem(i);
                    if(restoreFromAfterSearch.get(stack) == null){ //new item
                        extras.add(stack);
                    }
                }
                this.container.clearContent();
                for(Map.Entry<ItemStack, Integer> stackEntry : restoreFromAfterSearch.entrySet()){
                    this.container.setItem(stackEntry.getValue(), stackEntry.getKey());
                }
                for(ItemStack newStack : extras){
                    ItemSorting.addItem(container, newStack);
                }
                restoreFromAfterSearch.clear();
            }
        }else{
            NonNullList<ItemStack> matches = NonNullList.create();
            NonNullList<ItemStack> noMatches = NonNullList.create();
            boolean reset = restoreFromAfterSearch.isEmpty();
            for (int i = 0; i < this.container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty()) {
                    if(matchesSearch(player, search, stack)){
                        matches.add(stack);
                    }else{
                        noMatches.add(stack);
                    }
                }
                if(reset){
                    restoreFromAfterSearch.put(this.container.getItem(i), i);
                }
            }
            matches.sort(ItemSorting::defaultCompare);
            noMatches.sort(ItemSorting::defaultCompare);
            container.clearContent();
            matches.addAll(noMatches);
            for (int j = 0; j < container.getContainerSize() && j < matches.size(); j++) {
                container.setItem(j, matches.get(j));
            }
        }
        updateGrays(player, search);
        container.setChanged();
        broadcastChanges();
    }

    public boolean isSlotGray(int i) {
        if(getSlot(i) instanceof SlotCloudChest slot){
            return slot.isGray();
        }
        return false;
    }

}