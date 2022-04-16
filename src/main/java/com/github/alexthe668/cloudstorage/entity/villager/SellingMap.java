package com.github.alexthe668.cloudstorage.entity.villager;

import com.github.alexthe668.cloudstorage.world.CSConfiguredStructureRegistry;
import com.github.alexthe668.cloudstorage.world.CSStructureSetRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

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
    public BlockPos findNearestMapFeature(ServerLevel level, BlockPos p_207563_, int p_207564_, boolean p_207565_) {
        if (!level.getServer().getWorldData().worldGenSettings().generateFeatures()) {
            return null;
        } else {
            HolderSet<ConfiguredStructureFeature<?, ?>> holderset = HolderSet.direct(balloon ? CSConfiguredStructureRegistry.CONFIGURED_BIG_BALLOON_STRUCTURE : CSConfiguredStructureRegistry.CONFIGURED_SKY_TEMPLE_STRUCTURE);
            Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair = level.getChunkSource().getGenerator().findNearestMapFeature(level, holderset, p_207563_, p_207564_, p_207565_);
            return pair != null ? pair.getFirst() : null;
        }
    }

    @Nullable
    public MerchantOffer getOffer(Entity entity, Random random) {
        if (!(entity.level instanceof ServerLevel)) {
            return null;
        } else {
            ServerLevel serverlevel = (ServerLevel)entity.level;
            BlockPos blockpos = findNearestMapFeature(serverlevel, entity.blockPosition(), 100, true);
            if (blockpos != null) {
                ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
                MapItem.renderBiomePreviewMap(serverlevel, itemstack);
                MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
                itemstack.setHoverName(new TranslatableComponent(this.displayName));
                return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), itemstack, this.maxUses, this.villagerXp, 0.2F);
            } else {
                return null;
            }
        }
    }
}