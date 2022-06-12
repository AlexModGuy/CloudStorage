package com.github.alexthe668.cloudstorage.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class BadloonHandEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(BadloonHandEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> GESTURE = SynchedEntityData.defineId(BadloonHandEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PREV_GESTURE = SynchedEntityData.defineId(BadloonHandEntity.class, EntityDataSerializers.INT);
    public float gestureProgress = 1.0F;
    public float prevGestureProgress;
    public GloveGesture prevPrevGuesture;

    public BadloonHandEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public BadloonHandEntity(LivingEntity parent) {
        super(CSEntityRegistry.BADLOON_HAND.get(), parent.level);
        this.setParent(parent);
    }

    public BadloonHandEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(CSEntityRegistry.BADLOON_HAND.get(), world);
        this.setInvulnerable(true);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PARENT_UUID, Optional.empty());
        this.entityData.define(GESTURE, 0);
        this.entityData.define(PREV_GESTURE, 0);
    }

    public boolean shouldRender(double x, double y, double z) {
        Entity parent = this.getParent();
        return super.shouldRender(x, y, z) || parent != null && parent.shouldRender(parent.getX(), parent.getY(), parent.getZ());
    }

    public void tick(){
        if (prevPrevGuesture != this.getPrevGesture()) {
            prevPrevGuesture = this.getPrevGesture();
            gestureProgress = 0.0F;
        }
        prevGestureProgress = gestureProgress;
        if(!level.isClientSide){
            Entity parent = this.getParent();
            if(parent == null || parent instanceof LivingBalloon && !((LivingBalloon) parent).getChildId().equals(this.getUUID())){
                this.remove(RemovalReason.DISCARDED);
            }
            this.checkInsideBlocks();
            if(parent != null){
                Vec3 vector3d = new Vec3(parent.getX() - this.getX(), 0, parent.getZ() - this.getZ());
                float f = Mth.sqrt((float) (vector3d.x * vector3d.x + vector3d.z * vector3d.z));
                this.faceTowardsX((float) (f * 1 * (double) (180F / (float) Math.PI)));
                this.faceTowardsY(parent.getYRot());
                Vec3 newMovement = this.getDeltaMovement().add(this.moveTowardsParent(0.02F)).scale(0.9F);
                if(this.horizontalCollision && this.distanceTo(parent) > 2.0F){
                    double yUp = parent.getY() - this.getY();
                    newMovement = newMovement.add(0, yUp * 0.06F, 0);
                }else if(parent instanceof BalloonBuddyEntity && ((BalloonBuddyEntity) parent).isInSittingPose()){
                    if(parent.getY() - 1 < this.getY()){
                        newMovement = newMovement.add(0, -0.05F, 0);
                    }
                }
                if(newMovement.lengthSqr() > (double)1.0E-4F){
                    this.setDeltaMovement(newMovement);
                }
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
        }else{
            double d0 = this.getX() + getDeltaMovement().x;
            double d1 = this.getY() + getDeltaMovement().y;
            double d2 = this.getZ() + getDeltaMovement().z;
            this.setPosRaw(d0, d1, d2);
        }

        if (this.getPrevGesture() != this.getGesture() && gestureProgress < 1.0F) {
            gestureProgress += 0.25F;
        }
        if (this.getPrevGesture() == this.getGesture() && gestureProgress > 0F) {
            gestureProgress -= 0.25F;
        }
        if(!this.level.isClientSide && this.isVehicle()){
            setGesture(GloveGesture.GRAB);
        }
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }
    
    private void faceTowardsX(float f) {
        this.setXRot(BalloonFace.rotlerp(this.getXRot(), Mth.clamp(f, -75, 75), 25F));
    }
    private void faceTowardsY(float f) {
        this.setYRot(BalloonFace.rotlerp(this.getYRot(), f, 25F));
    }

    private Vec3 moveTowardsParent(float speed) {
        Entity parent = getParent();
        if(parent != null){
            float f = this.getGesture().holdsInFront() ? 0.5F : -0.5F;
            Vec3 gestAdd = new Vec3(0, 0, f).yRot(-this.getYRot() * ((float) Math.PI / 180F));
            Vec3 target = parent.position().subtract(0, 2F, 0).add(gestAdd).subtract(this.position());
            return target.normalize().scale(speed);
        }
        return Vec3.ZERO;
    }

    public boolean shouldRiderSit()
    {
        return false;
    }

    @Override
    public void positionRider(Entity entity) {
        entity.setPos(this.getX(), this.getY() + 0.4F - entity.getBbHeight(), this.getZ());
    }

    @Nullable
    public UUID getParentId() {
        return this.entityData.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getParent() {
        UUID id = getParentId();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    public void setParent(Entity entity) {
        this.setParentId(entity.getUUID());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("ParentUUID")) {
            this.setParentId(tag.getUUID("ParentUUID"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.getParentId() != null) {
            tag.putUUID("ParentUUID", this.getParentId());
        }
    }

    private void setGestureInt(int g) {
        this.entityData.set(GESTURE, g);
    }

    public void setGesture(GloveGesture gesture) {
        if(getGesture() != gesture){
            this.entityData.set(PREV_GESTURE, getGesture().ordinal());
        }
        this.setGestureInt(gesture.ordinal());
    }

    public GloveGesture getGesture() {
        return GloveGesture.values()[Mth.clamp(this.entityData.get(GESTURE), 0,  5)];
    }

    public GloveGesture getPrevGesture() {
        return GloveGesture.values()[Mth.clamp(this.entityData.get(PREV_GESTURE), 0,  5)];
    }

}
