package com.github.alexthe668.cloudstorage.block;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.misc.CSSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public abstract class AbstractCloudChestBlockEntity extends BlockEntity {
    private float openProgress;
    private float prevOpenProgress;
    private float emergence;
    private float prevEmergence;
    public int tickCount;
    public Player lastValidPlayer = null;
    private static Random random = new Random();
    private int lastSoundTimestamp = 0;

    public AbstractCloudChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, AbstractCloudChestBlockEntity e) {
        e.prevOpenProgress = e.openProgress;
        e.prevEmergence = e.emergence;
        e.tickCount++;
        boolean open = false;
        boolean emergedBalloon = false;
        Player player = e.level.getNearestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, CloudStorage.CONFIG.cloudChestOpenDistance.get(), false);
        if(player != null && e.hasClearance()){
            if(e.hasBalloonFor(player)){
                emergedBalloon = true;
                open = true;
                e.lastValidPlayer = player;
            }else if(!e.getValidBalloonStack(player).isEmpty()){
                open = true;
            }
        }

        if(open && e.openProgress == 0){
            e.playSound(true);
        }
        if(!open && e.openProgress == 1.0F){
            e.playSound(false);
        }
        if(open && e.openProgress < 1.0F){
            e.openProgress = Math.min(e.openProgress + 0.1F, 1.0F);
        }
        if(open && e.openProgress >= 1.0F){
            if(emergedBalloon && e.emergence < 1.0F){
                e.emergence = Math.min(e.emergence + 0.1F, 1.0F);
            }
            if(!emergedBalloon && e.emergence > 0.0F){
                e.emergence = Math.max(e.emergence - 0.1F, 0.0F);
            }
        }
        if(!open && e.openProgress > 0.0F){
            if(e.emergence <= 0.0F){
                e.openProgress = Math.max(e.openProgress - 0.1F, 0.0F);
            }else{
                e.emergence = Math.max(e.emergence - 0.1F, 0.0F);
            }
        }
        if(e.openProgress >= 1.0F && e.emergence > 0.5F && level.isClientSide){
            float radius = 3F;
            Vec3 center = Vec3.atCenterOf(e.getBlockPos());
            level.addParticle(CSParticleRegistry.CLOUD_CHEST, center.x + radius * (random.nextFloat() - 0.5F), center.y + radius * (random.nextFloat()), center.z + radius * (random.nextFloat() - 0.5F), center.x, center.y - 0.5F, center.z);
        }
    }

    private void playSound(boolean open){
        if(this.tickCount == 0 || this.lastSoundTimestamp < this.tickCount - 10){
            this.lastSoundTimestamp = this.tickCount;
            float pitch = 0.75F + random.nextFloat() * 0.35F;
            this.level.playSound((Player)null, this.getBlockPos(), open ? CSSoundRegistry.CLOUD_CHEST_OPEN : CSSoundRegistry.CLOUD_CHEST_CLOSE, SoundSource.BLOCKS, 1.0F, pitch);
        }
    }

    public AABB getRenderBoundingBox() {
        BlockPos pos = this.getBlockPos();
        return new AABB(pos.offset(-1, -1, -1), pos.offset(1, 3, 1));
    }

    public boolean hasClearance(){
        BlockPos pos = this.getBlockPos();
        return (level.canSeeSky(pos) || !CloudStorage.CONFIG.cloudChestNeedsSkyAccess.get()) && level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.above(2));
    }

    public float getOpenProgress(float partialTick) {
        return prevOpenProgress + (openProgress - prevOpenProgress) * partialTick;
    }

    public float getEmergence(float partialTick) {
        return prevEmergence + (emergence - prevEmergence) * partialTick;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }


    public MenuProvider getMenuProvider() {
        return new SimpleMenuProvider((id, playerInventory, textComponent) -> {
            return getMenu(id, playerInventory);
        }, getTitle());
    }


    public abstract boolean hasBalloonFor(Player player);

    public abstract int getBalloonFor(Player player);

    public abstract void setBalloonColorFor(Player player, int color);

    private ItemStack getValidBalloonStack(Player player) {
        ItemStack itemStack1 = player.getMainHandItem();
        ItemStack itemStack2 = player.getOffhandItem();
        if(itemStack1.is(CSItemRegistry.BALLOON.get())){
            return itemStack1;
        }
        if(itemStack2.is(CSItemRegistry.BALLOON.get())){
            return itemStack2;
        }
        return ItemStack.EMPTY;
    }

    public abstract AbstractContainerMenu getMenu(int i, Inventory playerInventory);

    public abstract Component getTitle();

    public abstract boolean hasNoInvSpace(Player player);

    public abstract void releaseBalloons();

    public abstract int getContainerSize(Player player);

    public void setLootBalloon(int color, ResourceLocation resourceLocation, long seed){}

    public boolean hasLootBalloon(){
        return false;
    }
}
