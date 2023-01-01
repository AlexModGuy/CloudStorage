package com.github.alexthe668.cloudstorage.entity;

import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.List;

public class BalloonTieEntity extends Entity {

    private static final EntityDataAccessor<Integer> BALLOON_COUNT = SynchedEntityData.defineId(BalloonTieEntity.class, EntityDataSerializers.INT);
    private float popTick = 0.0F;
    private Vec3 randomMoveOffset = null;

    public BalloonTieEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public BalloonTieEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(CSEntityRegistry.BALLOON_TIE.get(), world);
    }

    public BalloonTieEntity(Level level, BlockPos pos) {
        this(CSEntityRegistry.BALLOON_TIE.get(), level);
        this.setPos(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
    }

    public void tick(){
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if(!level.isClientSide && tickCount > 5 && (this.getBalloonCount() <= 0 || !this.getBlockStateOn().is(BlockTags.FENCES) && !this.getBlockStateOn().is(CSBlockRegistry.BALLOON_STAND.get()))){
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    protected void pushEntities() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            for(int l = 0; l < list.size(); ++l) {
                Entity entity = list.get(l);
                this.push(entity);
            }
        }
    }

    public boolean isPushable() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BALLOON_COUNT, 0);
    }

    public void setBalloonCount(int count) {
        this.entityData.set(BALLOON_COUNT, count);
    }

    public int getBalloonCount() {
        return this.entityData.get(BALLOON_COUNT);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    public boolean isPickable() {
        return true;
    }

    public boolean hurt(DamageSource source, float f) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.markHurt();
            this.remove(RemovalReason.KILLED);
            return true;
        }
    }

    public static BalloonTieEntity getOrCreateKnot(Level level, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        if(!level.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D)).isEmpty()){
            return null;
        }

        for(BalloonTieEntity leashfenceknotentity : level.getEntitiesOfClass(BalloonTieEntity.class, new AABB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D))) {
            if (leashfenceknotentity.blockPosition().equals(pos)) {
                return leashfenceknotentity;
            }
        }

        BalloonTieEntity leashfenceknotentity1 = new BalloonTieEntity(level, pos);
        if(level.getBlockState(pos).getBlock() == CSBlockRegistry.BALLOON_STAND.get()){
            leashfenceknotentity1.setPos(leashfenceknotentity1.getX(), leashfenceknotentity1.getY() + 0.3F, leashfenceknotentity1.getZ());
        }
        level.addFreshEntity(leashfenceknotentity1);
        return leashfenceknotentity1;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setBalloonCount(compound.getInt("BalloonCount"));

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("BalloonCount", this.getBalloonCount());
    }

    public boolean skipAttackInteraction(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            return !this.level.mayInteract(player, this.blockPosition()) ? true : this.hurt(DamageSource.playerAttack(player), 0.0F);
        } else {
            return false;
        }
    }

    public ItemStack getPickResult() {
        return null;
    }

}

