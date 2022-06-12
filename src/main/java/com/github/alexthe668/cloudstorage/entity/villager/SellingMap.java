package com.github.alexthe668.cloudstorage.entity.villager;

import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.world.CSStructureRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;

public class SellingMap implements VillagerTrades.ItemListing {
    private final int emeraldCost;
    private final boolean balloon;
    private final String displayName;
    private final MapDecoration.Type destinationType;
    private final int maxUses;
    private final int villagerXp;

    public SellingMap(int cost, boolean balloon, String displayName, MapDecoration.Type destinationType, int uses, int exp) {
        this.emeraldCost = cost;
        this.balloon = balloon;
        this.displayName = displayName;
        this.destinationType = destinationType;
        this.maxUses = uses;
        this.villagerXp = exp;
    }

    @Nullable
    public BlockPos findNearestMapFeature(ServerLevel level, BlockPos pos, int dist, boolean bool) {
        if (!level.getServer().getWorldData().worldGenSettings().generateStructures()) {
            return null;
        } else {
            try{
                Registry<Structure> registry = level.getLevel().registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
                Holder<Structure> holder = balloon ? registry.getHolderOrThrow(CSStructureRegistry.BIG_BALLOON_KEY) : registry.getHolderOrThrow(CSStructureRegistry.SKY_TEMPLE_KEY);
                HolderSet<Structure> holderset = HolderSet.direct(holder);
                Pair<BlockPos, Holder<Structure>> pair = level.getChunkSource().getGenerator().findNearestMapStructure(level, holderset, pos, 100, false);
                return pair == null ? null : pair.getFirst();
            }catch (Exception e){
                return null;
            }
        }
    }

    @Nullable
    public MerchantOffer getOffer(Entity entity, RandomSource random) {
        if (!(entity.level instanceof ServerLevel)) {
            return null;
        } else {
            ServerLevel serverlevel = (ServerLevel)entity.level;
            BlockPos blockpos = findNearestMapFeature(serverlevel, entity.blockPosition(), 100, true);
            if (blockpos != null) {
                ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
                MapItem.renderBiomePreviewMap(serverlevel, itemstack);
                MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
                CompoundTag compoundtag1 = itemstack.getOrCreateTagElement("display");
                compoundtag1.putInt("MapColor", balloon ? BalloonItem.DEFAULT_COLOR : 0XA6B3BF);
                itemstack.setHoverName(Component.translatable(this.displayName));
                return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(balloon ? CSItemRegistry.BALLOON.get() : CSBlockRegistry.CLOUD.get()), itemstack, this.maxUses, this.villagerXp, 0.2F);
            } else {
                return null;
            }
        }
    }
}