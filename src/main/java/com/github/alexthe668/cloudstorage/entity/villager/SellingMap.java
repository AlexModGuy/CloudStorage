package com.github.alexthe668.cloudstorage.entity.villager;

import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.world.CSConfiguredStructureRegistry;
import com.github.alexthe668.cloudstorage.world.CSStructureSetRegistry;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;
import java.util.*;

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
        if (!level.getServer().getWorldData().worldGenSettings().generateFeatures()) {
            return null;
        } else {
            try{
                Registry<ConfiguredStructureFeature<?, ?>> registry = level.getLevel().registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
                Holder<ConfiguredStructureFeature<?, ?>> holder = balloon ? registry.getHolder(CSConfiguredStructureRegistry.CONFIGURED_BIG_BALLOON).get() : registry.getHolder(CSConfiguredStructureRegistry.CONFIGURED_SKY_TEMPLE).get();
                HolderSet<ConfiguredStructureFeature<?, ?>> holderset = HolderSet.direct(holder);
                Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair = level.getChunkSource().getGenerator().findNearestMapFeature(level, holderset, pos, 100, false);
                return pair == null ? null : pair.getFirst();
            }catch (Exception e){
                return null;
            }
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
                CompoundTag compoundtag1 = itemstack.getOrCreateTagElement("display");
                compoundtag1.putInt("MapColor", balloon ? BalloonItem.DEFAULT_COLOR : 0XA6B3BF);
                itemstack.setHoverName(new TranslatableComponent(this.displayName));
                return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(balloon ? CSItemRegistry.BALLOON.get() : CSBlockRegistry.CLOUD.get()), itemstack, this.maxUses, this.villagerXp, 0.2F);
            } else {
                return null;
            }
        }
    }
}