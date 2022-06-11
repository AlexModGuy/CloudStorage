package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.inventory.CloudChestContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;

import java.util.UUID;

public class CloudIndex {

    private UUID playerUUID;
    private int balloonColor;
    private int containerSize;
    private CloudChestContainer container;

    public CloudIndex(UUID playerUUID, int balloonColor, int size){
        this.playerUUID = playerUUID;
        this.balloonColor = balloonColor;
        this.containerSize = size;
        this.container = new CloudChestContainer(size);
    }

    public CloudIndex(CompoundTag innerTag) {
        this.balloonColor = innerTag.getInt("BalloonColor");
        this.containerSize = innerTag.getInt("Size");
        this.playerUUID = innerTag.getUUID("PlayerUUID");
        this.container = new CloudChestContainer(this.containerSize);
        this.container.fromTag(innerTag.getList("ContainerTag", 10));
    }

    public void writeToNBT(CompoundTag tag) {
        tag.putInt("BalloonColor", balloonColor);
        tag.putInt("Size", containerSize);
        tag.putUUID("PlayerUUID", playerUUID);
        tag.put("ContainerTag", this.container.createTag());
    }

    public void resize(int newSize){
        if(this.containerSize != newSize){
            ListTag tag = new ListTag();
            if(this.container != null){
                tag = this.container.createTag();
            }
            this.containerSize = newSize;
            this.container = new CloudChestContainer(newSize);
            this.container.fromTag(tag);
        }
    }

    public int getBalloonColor() {
        return balloonColor;
    }

    public void setBalloonColor(int balloonColor) {
        this.balloonColor = balloonColor;
    }

    public int getContainerSize() {
        return containerSize;
    }

    public SimpleContainer getContainer() {
        return container;
    }

    public UUID getPlayerUUID(){
        return playerUUID;
    }

    public int calcUsedSlots() {
        int slots = 0;
        for(int i = 0; i < container.getContainerSize(); i++){
            if(!container.getItem(i).isEmpty()){
                slots++;
            }
        }
        return slots;
    }
}
