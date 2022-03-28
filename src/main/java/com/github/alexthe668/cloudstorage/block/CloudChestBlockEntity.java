package com.github.alexthe668.cloudstorage.block;

import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.inventory.CloudChestMenu;
import com.github.alexthe668.cloudstorage.misc.CSWorldData;
import com.github.alexthe668.cloudstorage.misc.CloudIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CloudChestBlockEntity extends AbstractCloudChestBlockEntity {

    private static final Component CONTAINER_TITLE = new TranslatableComponent("cloudstorage.container.cloud_chest");
    public Map<UUID, Integer> playerToBalloonColor = new HashMap<>();
    public int tickCount;
    public Player lastValidPlayer = null;

    public CloudChestBlockEntity(BlockPos pos, BlockState state) {
        super(CSBlockEntityRegistry.CLOUD_CHEST.get(), pos, state);
    }

    @Override
    public boolean hasBalloonFor(Player player) {
       return playerToBalloonColor.containsKey(player.getUUID());
    }

    @Override
    public int getBalloonFor(Player player) {
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
    }

    @Override
    public AbstractContainerMenu getMenu(int i, Inventory playerInventory) {
        CSWorldData data = CSWorldData.get(getLevel());
        Player player = playerInventory.player;
        if(data != null && this.hasBalloonFor(player)){
            CloudIndex cloudIndex = data.getPrivateCloud(player.getUUID(), this.getBalloonFor(player));
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
            CloudIndex cloudIndex = data.getPrivateCloud(player.getUUID(), this.getBalloonFor(player));
            return cloudIndex == null || cloudIndex.getContainerSize() == 0;
        }
        return false;
    }

    @Override
    public void releaseBalloons() {
        Vec3 releasePosition = Vec3.atBottomCenterOf(this.getBlockPos()).add(0, getEmergence(1.0F) * 2F, 0);
        for(Integer color : playerToBalloonColor.values()){
            BalloonEntity balloon = CSEntityRegistry.BALLOON.get().create(level);
            balloon.setBalloonColor(color);
            balloon.setStringLength(BalloonEntity.DEFAULT_STRING_LENGTH);
            balloon.setPos(releasePosition);
            level.addFreshEntity(balloon);
        }
    }
}
