package com.github.alexthe668.cloudstorage.entity.villager;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;

import java.util.Random;

public class SellingRandomlyDyedItem implements VillagerTrades.ItemListing {
    private final ItemStack sellingItem;
    private final int emeraldCount;
    private final int sellingItemCount;
    private final int maxUses;
    private final int xpValue;
    private final float priceMultiplier;

    public SellingRandomlyDyedItem(Block sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
        this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, maxUses, xpValue);
    }

    public SellingRandomlyDyedItem(Item sellingItem, int emeraldCount, int sellingItemCount, int xpValue) {
        this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, 12, xpValue);
    }

    public SellingRandomlyDyedItem(Item sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
        this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, maxUses, xpValue);
    }

    public SellingRandomlyDyedItem(ItemStack sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
        this(sellingItem, emeraldCount, sellingItemCount, maxUses, xpValue, 0.05F);
    }

    public SellingRandomlyDyedItem(ItemStack sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue, float priceMultiplier) {
        this.sellingItem = sellingItem;
        this.emeraldCount = emeraldCount;
        this.sellingItemCount = sellingItemCount;
        this.maxUses = maxUses;
        this.xpValue = xpValue;
        this.priceMultiplier = priceMultiplier;
    }

    public MerchantOffer getOffer(Entity trader, Random rand) {
        ItemStack dyed = this.sellingItem.copy();
        dyed.setCount(sellingItemCount);
        if(dyed.getItem() instanceof DyeableLeatherItem){
            ((DyeableLeatherItem)dyed.getItem()).setColor(dyed, (int)(rand.nextFloat() * 0xFFFFFF));
        }
        return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCount), dyed, this.maxUses, this.xpValue, this.priceMultiplier);
    }
}
