package com.github.alexthe668.cloudstorage.block;

import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.inventory.CloudChestMenu;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.misc.CloudIndex;
import com.github.alexthe668.cloudstorage.world.CSWorldData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CloudChestBlockEntity extends AbstractCloudChestBlockEntity {

    private static final Component CONTAINER_TITLE = Component.translatable("cloudstorage.container.cloud_chest");
    private Map<UUID, Integer> playerToBalloonColor = new HashMap<>();
    private int lootBalloonColor = 0;
    private ResourceLocation lootTable;
    protected long lootTableSeed;

    public CloudChestBlockEntity(BlockPos pos, BlockState state) {
        super(CSBlockEntityRegistry.CLOUD_CHEST.get(), pos, state);
    }

    @Override
    public boolean hasBalloonFor(Player player) {
       return playerToBalloonColor.containsKey(player.getUUID()) || hasLootBalloon();
    }

    @Override
    public int getBalloonFor(Player player) {
        if(hasLootBalloon()){
            return this.lootBalloonColor + 1;
        }
        return playerToBalloonColor.get(player.getUUID());
    }

    @Override
    public void setBalloonColorFor(Player player, int color) {
        playerToBalloonColor.put(player.getUUID(), color);
        this.setChanged();
    }


    public void load(CompoundTag tag) {
        super.load(tag);
        ListTag list = tag.getList("PlayerBalloons", 10);
        if(!list.isEmpty()){
            for(int i = 0; i < list.size(); ++i) {
                CompoundTag compoundtag = list.getCompound(i);
                UUID uuid = compoundtag.getUUID("UUID");
                if(uuid != null){
                    playerToBalloonColor.put(uuid, compoundtag.getInt("BalloonColor"));
                }
            }
        }
        lootBalloonColor = tag.getInt("LootColor");
        tryLoadLootTable(tag);
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag list = new ListTag();
        for(Map.Entry<UUID, Integer> entry : playerToBalloonColor.entrySet()){
            CompoundTag balloonData = new CompoundTag();
            balloonData.putUUID("UUID", entry.getKey());
            balloonData.putInt("BalloonColor", entry.getValue());
            list.add(balloonData);
        }
        tag.put("PlayerBalloons", list);
        tag.putInt("LootColor", lootBalloonColor);
        trySaveLootTable(tag);
    }

    protected boolean tryLoadLootTable(CompoundTag tag) {
        if (tag.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(tag.getString("LootTable"));
            this.lootTableSeed = tag.getLong("LootTableSeed");
            return true;
        } else {
            return false;
        }
    }

    protected boolean trySaveLootTable(CompoundTag tag) {
        if (this.lootTable == null) {
            return false;
        } else {
            tag.putString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                tag.putLong("LootTableSeed", this.lootTableSeed);
            }
            return true;
        }
    }

    @Override
    public AbstractContainerMenu getMenu(int i, Inventory playerInventory) {
        CSWorldData data = CSWorldData.get(getLevel());
        Player player = playerInventory.player;
        if(data != null && this.hasBalloonFor(player) && !hasNoInvSpace(player)){
            CloudIndex cloudIndex = getOrInitializePrivateCloud(data, player);
            if(cloudIndex != null && cloudIndex.getContainerSize() > 0){
                return new CloudChestMenu(i, playerInventory, cloudIndex.getContainer());
            }
        }
        return null;
    }

    @Override
    public Component getTitle() {
        return CONTAINER_TITLE;
    }

    @Override
    public boolean hasNoInvSpace(Player player) {
        CSWorldData data = CSWorldData.get(getLevel());
        if(data != null && this.hasBalloonFor(player)){
            CloudIndex cloudIndex = getOrInitializePrivateCloud(data, player);
            return cloudIndex == null || cloudIndex.getContainerSize() == 0;
        }
        return false;
    }

    @Override
    public void releaseBalloons() {
        Vec3 releasePosition = Vec3.atBottomCenterOf(this.getBlockPos()).add(0, getEmergence(1.0F) * 2F, 0);
        if(hasLootBalloon()){
            ItemStack stack = new ItemStack(CSItemRegistry.BALLOON.get());
            BalloonItem.setLoot(stack, true);
            ((DyeableLeatherItem)stack.getItem()).setColor(stack, lootBalloonColor + 1);
            ItemEntity dropped = new ItemEntity(level, releasePosition.x, releasePosition.y, releasePosition.z, stack);
            level.addFreshEntity(dropped);
        }else {
            for(Map.Entry<UUID, Integer> entry : playerToBalloonColor.entrySet()){
                BalloonEntity balloon = CSEntityRegistry.BALLOON.get().create(level);
                balloon.setBalloonColor(entry.getValue());
                balloon.setCharged(false);
                balloon.setStringLength(BalloonEntity.DEFAULT_STRING_LENGTH);
                balloon.setPos(releasePosition);
                level.addFreshEntity(balloon);
            }
        }
    }

    @Override
    public int getContainerSize(Player player) {
        CSWorldData data = CSWorldData.get(getLevel());
        if (data != null && this.hasBalloonFor(player)) {
            CloudIndex cloudIndex = getOrInitializePrivateCloud(data, player);
            return cloudIndex != null ? cloudIndex.getContainerSize() : 0;
        }
        return 0;
    }

    @Nullable
    private CloudIndex getOrInitializePrivateCloud(CSWorldData data, Player player){
        if(hasLootBalloon()){
            CloudIndex indx = data.getLootCloud(player.getUUID(), this.lootBalloonColor + 1);
            if(indx == null){
                CloudIndex lootable = new CloudIndex(player.getUUID(), this.lootBalloonColor + 1, 27);
                unpackLootTable(player, lootable.getContainer());
                data.addLootCloud(lootable);
                return lootable;
            }else{
                return indx;
            }
        }else{
            return data.getPrivateCloud(player.getUUID(), this.getBalloonFor(player));
        }
    }

    public void setLootBalloon(int color, ResourceLocation resourceLocation, long seed){
        this.lootBalloonColor = color - 1;
        this.lootTable = resourceLocation;
        this.lootTableSeed = seed;
    }

    public boolean hasLootBalloon(){
        return this.lootBalloonColor != 0;
    }

    public void unpackLootTable(@Nullable Player player, Container toFill) {
        if (this.lootTable != null && this.level.getServer() != null) {
            LootTable loottable = this.level.getServer().getLootTables().get(this.lootTable);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)player, this.lootTable);
            }
            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withOptionalRandomSeed(this.lootTableSeed);
            if (player != null) {
                lootcontext$builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }
            loottable.fill(toFill, lootcontext$builder.create(LootContextParamSets.CHEST));
        }
    }


}
