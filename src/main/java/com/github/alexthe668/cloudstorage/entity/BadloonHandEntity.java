package com.github.alexthe668.cloudstorage.entity;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

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
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
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
        this.setOldPosAndRot();
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
                if(parent.isPassenger()){
                    Entity vehicle = parent.getVehicle();
                    double below = vehicle.getY() + vehicle.getPassengersRidingOffset() - 0.2F;
                    this.setPos(parent.getX(), below, parent.getZ());
                    this.setDeltaMovement(Vec3.ZERO);
                    this.faceTowardsY(vehicle.getYRot());
                    this.setGesture(GloveGesture.GRAB);
                }else {
                    Vec3 vector3d = new Vec3(parent.getX() - this.getX(), 0, parent.getZ() - this.getZ());
                    float f = Mth.sqrt((float) (vector3d.x * vector3d.x + vector3d.z * vector3d.z));
                    this.faceTowardsX((float) (f * 1 * (double) (180F / (float) Math.PI)));
                    this.faceTowardsY(parent.getYRot());
                    Vec3 newMovement = this.getDeltaMovement().add(this.moveTowardsParent(0.02F)).scale(0.9F);
                    if (this.horizontalCollision && this.distanceTo(parent) > 2.0F) {
                        double yUp = parent.getY() - this.getY();
                        newMovement = newMovement.add(0, yUp * 0.06F, 0);
                    } else if (parent instanceof BalloonBuddyEntity && ((BalloonBuddyEntity) parent).isInSittingPose()) {
                        if (parent.getY() - 1 < this.getY()) {
                            newMovement = newMovement.add(0, -0.05F, 0);
                        }
                    }
                    if (newMovement.lengthSqr() > (double) 1.0E-4F) {
                        this.setDeltaMovement(newMovement);
                    }
                }
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
        }else{
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double)this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double)this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double)this.lSteps;
                double d2 = Mth.wrapDegrees(this.lyr - (double)this.getYRot());
                this.setYRot(this.getYRot() + (float)d2 / (float)this.lSteps);
                this.setXRot(this.getXRot() + (float)(this.lxr - (double)this.getXRot()) / (float)this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
                this.setRot(this.getYRot(), this.getXRot());
            } else {
                this.reapplyPosition();
                this.setRot(this.getYRot(), this.getXRot());
            }
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

    //compat with domestication innovation
    protected void checkInsideBlocks() {
        Entity parent = getParent();
        AABB aabb = this.getBoundingBox();
        BlockPos blockpos = new BlockPos(aabb.minX + 0.001D, aabb.minY + 0.001D, aabb.minZ + 0.001D);
        BlockPos blockpos1 = new BlockPos(aabb.maxX - 0.001D, aabb.maxY - 0.001D, aabb.maxZ - 0.001D);
        if (this.level.hasChunksAt(blockpos, blockpos1)) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            for(int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
                for(int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                    for(int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                        blockpos$mutableblockpos.set(i, j, k);
                        BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);

                        try {
                            blockstate.entityInside(this.level, blockpos$mutableblockpos, this);
                            this.onInsideBlock(blockstate);
                            if(parent != null){
                                blockstate.entityInside(this.level, blockpos$mutableblockpos, parent);
                            }
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.forThrowable(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being collided with");
                            CrashReportCategory.populateBlockDetails(crashreportcategory, this.level, blockpos$mutableblockpos, blockstate);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
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

    public void lerpTo(double p_38102_, double p_38103_, double p_38104_, float p_38105_, float p_38106_, int p_38107_, boolean p_38108_) {
        this.lx = p_38102_;
        this.ly = p_38103_;
        this.lz = p_38104_;
        this.lyr = (double)p_38105_;
        this.lxr = (double)p_38106_;
        this.lSteps = p_38107_;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    public void lerpMotion(double p_38171_, double p_38172_, double p_38173_) {
        this.lxd = p_38171_;
        this.lyd = p_38172_;
        this.lzd = p_38173_;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }
}
