package com.github.alexthe668.cloudstorage.inventory;

import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BalloonStandMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    long lastSoundTime;
    public final Container container = new SimpleContainer(2) {
        public void setChanged() {
            BalloonStandMenu.this.slotsChanged(this);
            super.setChanged();
        }
    };
    private final ResultContainer resultContainer = new ResultContainer() {
        public void setChanged() {
            BalloonStandMenu.this.slotsChanged(this);
            super.setChanged();
        }
    };

    public BalloonStandMenu(int p_39140_, Inventory p_39141_) {
        this(p_39140_, p_39141_, ContainerLevelAccess.NULL);
    }

    public BalloonStandMenu(int id, Inventory inventory, final ContainerLevelAccess access) {
        super(CSMenuRegistry.BALLOON_STAND_MENU.get(), id);
        this.access = access;
        this.addSlot(new Slot(this.container, 0, 77, 16) {
            public boolean mayPlace(ItemStack stack) {
                return stack.is(CSItemRegistry.BALLOON.get()) && !BalloonItem.isLoot(stack);
            }
        });
        this.addSlot(new Slot(this.container, 1, 77, 53) {
            public boolean mayPlace(ItemStack stack) {
                return stack.is(CSItemRegistry.BALLOON.get()) && !BalloonItem.isLoot(stack);
            }
        });
        this.addSlot(new Slot(this.resultContainer, 2, 137, 35) {
            public boolean mayPlace(ItemStack p_39217_) {
                return false;
            }

            public void onTake(Player player, ItemStack stack) {
                BalloonStandMenu.this.slots.get(0).remove(1);
                BalloonStandMenu.this.slots.get(1).remove(1);
                stack.getItem().onCraftedBy(stack, player.level, player);
                access.execute((level, pos) -> {
                    long l = level.getGameTime();
                    if (BalloonStandMenu.this.lastSoundTime != l) {
                        level.playSound((Player) null, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        BalloonStandMenu.this.lastSoundTime = l;
                    }

                });
                super.onTake(player, stack);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 94 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 152));
        }

    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, CSBlockRegistry.BALLOON_STAND.get());
    }

    public void slotsChanged(Container container) {
        ItemStack itemstack = this.container.getItem(0);
        ItemStack itemstack1 = this.container.getItem(1);
        ItemStack itemstack2 = this.resultContainer.getItem(2);
        if (itemstack2.isEmpty() || !itemstack.isEmpty() && !itemstack1.isEmpty()) {
            if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
                this.setupResultSlot(itemstack, itemstack1, itemstack2);
            }
        } else {
            this.resultContainer.removeItemNoUpdate(2);
        }

    }

    private void setupResultSlot(ItemStack top, ItemStack bottom, ItemStack result) {
        this.access.execute((p_39170_, p_39171_) -> {
            if(top.is(CSItemRegistry.BALLOON.get()) && bottom.is(CSItemRegistry.BALLOON.get())){
                ItemStack itemstack = top.copy();
                itemstack.setCount(2);
                if (!ItemStack.matches(itemstack, result)) {
                    this.resultContainer.setItem(2, itemstack);
                    this.broadcastChanges();
                }
            }
        });
    }

    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(stack, slot);
    }

    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotIndex == 2) {
                itemstack1.getItem().onCraftedBy(itemstack1, player.level, player);
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (slotIndex != 1 && slotIndex != 0) {
                if (itemstack1.is(CSItemRegistry.BALLOON.get())) {
                    if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(2);
        this.access.execute((p_39152_, p_39153_) -> {
            this.clearContainer(player, this.container);
        });
    }
}
