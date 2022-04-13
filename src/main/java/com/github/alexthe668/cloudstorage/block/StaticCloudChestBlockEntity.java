package com.github.alexthe668.cloudstorage.block;

import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.inventory.CloudChestMenu;
import com.github.alexthe668.cloudstorage.misc.CSWorldData;
import com.github.alexthe668.cloudstorage.misc.CloudIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class StaticCloudChestBlockEntity extends AbstractCloudChestBlockEntity {

    private static final Component CONTAINER_TITLE = new TranslatableComponent("cloudstorage.container.static_cloud_chest");
    private int balloonColor = -1;
    private boolean balloonStatic = false;
    private net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler> input = LazyOptional.empty();

    public StaticCloudChestBlockEntity(BlockPos pos, BlockState state) {
        super(CSBlockEntityRegistry.STATIC_CLOUD_CHEST.get(), pos, state);
    }

    @Override
    public boolean hasBalloonFor(Player player) {
        return balloonColor != -1;
    }

    @Override
    public int getBalloonFor(Player player) {
        return balloonColor;
    }

    @Override
    public boolean getBalloonStaticFor(Player player) { return balloonStatic; }

    @Override
    public void setBalloonColorFor(Player player, int color) {
        balloonColor = color;
        this.setChanged();
    }

    @Override
    public void setBalloonStaticFor(Player player, boolean isStatic) {
        balloonStatic = isStatic;
        this.setChanged();
    }

    @Override
    public AbstractContainerMenu getMenu(int i, Inventory playerInventory) {
        CSWorldData data = CSWorldData.get(getLevel());
        Player player = playerInventory.player;
        if (data != null && this.hasBalloonFor(player)) {
            CloudIndex cloudIndex = data.getPublicCloud(this.getBalloonFor(player));
            if (cloudIndex != null && cloudIndex.getContainerSize() > 0) {
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
        if (data != null && this.hasBalloonFor(player)) {
            CloudIndex cloudIndex = data.getPublicCloud(this.getBalloonFor(player));
            return cloudIndex == null || cloudIndex.getContainerSize() == 0;
        }
        return false;
    }


    public void load(CompoundTag tag) {
        super.load(tag);
        balloonColor = tag.getInt("BalloonColor");
        balloonStatic = tag.getBoolean("BalloonStatic");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BalloonColor", balloonColor);
        tag.putBoolean("BalloonStatic", balloonStatic);
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(!this.hasNoInvSpace(null)){
                if(!input.isPresent()){
                    Container cloudContainer = getCloudContainer();
                    if(cloudContainer != null){
                        input = LazyOptional.of(() -> new InvWrapper(cloudContainer));
                    }
                }
                return input.cast();
            }
        }
        return super.getCapability(capability, facing);
    }

    private Container getCloudContainer(){
        CSWorldData data = CSWorldData.get(getLevel());
        if (data != null && this.hasBalloonFor(null)) {
            CloudIndex cloudIndex = data.getPublicCloud(this.getBalloonFor(null));
            if (cloudIndex != null && cloudIndex.getContainerSize() > 0) {
                return cloudIndex.getContainer();
            }
        }
        return null;
    }

    @Override
    public void releaseBalloons() {
        if(this.balloonColor != -1){
            Vec3 releasePosition = Vec3.atBottomCenterOf(this.getBlockPos()).add(0, getEmergence(1.0F) * 2F, 0);
            BalloonEntity balloon = CSEntityRegistry.BALLOON.get().create(level);
            balloon.setBalloonColor(this.balloonColor);
            balloon.setCharged(this.balloonStatic);
            balloon.setStringLength(BalloonEntity.DEFAULT_STRING_LENGTH);
            balloon.setPos(releasePosition);
            level.addFreshEntity(balloon);
        }
    }

    @Override
    public int getContainerSize(Player player) {
        CSWorldData data = CSWorldData.get(getLevel());
        if (data != null && this.hasBalloonFor(player)) {
            CloudIndex cloudIndex = data.getPublicCloud(this.getBalloonFor(player));
            return cloudIndex != null ? cloudIndex.getContainerSize() : 0;
        }
        return 0;
    }
}
