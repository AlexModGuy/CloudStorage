package com.github.alexthe668.cloudstorage.entity;

import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.entity.ai.BadloonAttackGoal;
import com.github.alexthe668.cloudstorage.entity.ai.BadloonFearCactusGoal;
import com.github.alexthe668.cloudstorage.entity.ai.FlightMoveController;
import com.github.alexthe668.cloudstorage.entity.ai.FlyAroundGoal;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.misc.CSSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

public class BadloonEntity extends Monster implements LivingBalloon, BalloonFlyer {

    private static final EntityDataAccessor<Float> ROT_Z = SynchedEntityData.defineId(BadloonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(BadloonEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(BadloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FACE = SynchedEntityData.defineId(BadloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BALLOON_COLOR = SynchedEntityData.defineId(BadloonEntity.class, EntityDataSerializers.INT);
    public boolean dropMusicDisk = false;
    private Vec3 randomMoveOffset = null;
    public float prevRotZ;
    public int fearOfBeingPoppedCooldown = 0;
    private int droppedItems = 0;

    protected BadloonEntity(EntityType type, Level level) {
        super(type, level);
        this.xpReward = 3;
        this.moveControl = new FlightMoveController(this, 1F, true);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_CACTUS, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_CACTUS, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {}

    protected void updateNoActionTime() {
        if(random.nextBoolean()){
            this.noActionTime += 1;
        }
    }

    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn) {
            public boolean isStableDestination(BlockPos pos) {
                return this.level.getBlockState(pos).isAir();
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BadloonFearCactusGoal(this, 1.3F));
        this.goalSelector.addGoal(1, new BadloonAttackGoal(this));
        this.goalSelector.addGoal(2, new FlyAroundGoal(this, 15, 7, 30, 1.0F));
        this.targetSelector.addGoal(1, (new NearestAttackableTargetGoal<>(this, Player.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        fallDistance = 0;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 2.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.15F).add(Attributes.FLYING_SPEED, 0.15F).add(Attributes.FOLLOW_RANGE, 32D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHILD_UUID, Optional.empty());
        this.entityData.define(CHILD_ID, -1);
        this.entityData.define(FACE, 0);
        this.entityData.define(ROT_Z, 0F);
        this.entityData.define(BALLOON_COLOR, BalloonItem.DEFAULT_COLOR);
    }


    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ChildUUID")) {
            this.setChildId(compound.getUUID("ChildUUID"));
        }
        this.setFaceInt(compound.getInt("Face"));
        if(compound.contains("BalloonColor")){
            this.setBalloonColor(compound.getInt("BalloonColor"));
        }else {
            this.setBalloonColor(BalloonItem.DEFAULT_COLOR);
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getChildId() != null) {
            compound.putUUID("ChildUUID", this.getChildId());
        }
        compound.putInt("Face", this.getFace().ordinal());
        compound.putInt("BalloonColor", this.getBalloonColor());
    }

    @Override
    public void tick(){
        super.tick();
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.8F, 0.6F, 0.8F));
        this.prevRotZ = this.getRotZ();
        if (!level.isClientSide) {
            Entity child = getChild();
            if (child == null) {
                BadloonHandEntity hand = new BadloonHandEntity(this);
                hand.setPos(this.position().add(0, 0.5F, 0));
                level.addFreshEntity(hand);
                this.setChildId(hand.getUUID());
                this.entityData.set(CHILD_ID, hand.getId());
            }else{
                this.entityData.set(CHILD_ID, child.getId());
                if(this.distanceTo(child) > 4){
                    Vec3 back = child.position().subtract(this.position()).normalize();
                    this.setDeltaMovement(this.getDeltaMovement().add(back.scale(0.1F)));
                }
            }
            if(randomMoveOffset != null){
                Vec3 add = randomMoveOffset.normalize().scale(0.01F + random.nextFloat() * 0.01F);
                this.setDeltaMovement(this.getDeltaMovement().add(add));
            }
            if(this.isOnGround()){
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.08, 0));
            }
            if(fearOfBeingPoppedCooldown > 0){
                fearOfBeingPoppedCooldown--;
            }
        }
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        float f = Mth.sqrt((float) (vector3d.x * vector3d.x + vector3d.z * vector3d.z));
        if(!level.isClientSide){
            float xRotTarget = (float) (Mth.atan2(vector3d.y, f) * 0.05F * (double) (180F / (float) Math.PI));
            this.setXRot(BalloonFace.rotlerp(this.getXRot(), xRotTarget, 5F));
        }
        float threshold = 0.015F;
        if(this.yRotO - this.getYRot() > threshold) {
            this.setRotZ(this.getRotZ() + 1);
        } else if (this.yRotO - this.getYRot() < -threshold) {
            this.setRotZ(this.getRotZ() - 1);
        } else if (this.getRotZ() > 0) {
            this.setRotZ(Math.max(this.getRotZ() - 3, 0));
        } else if (this.getRotZ() < 0) {
            this.setRotZ(Math.min(this.getRotZ() + 3, 0));
        }
    }
    @Override
    public boolean isNoGravity() {
        return true;
    }

    public Entity getChild() {
        UUID id = getChildId();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    @Nullable
    public UUID getChildId() {
        return this.entityData.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getHandForRendering() {
        return this.level.getEntity(this.entityData.get(CHILD_ID));
    }

    private void setFaceInt(int face) {
        this.entityData.set(FACE, face % BalloonFace.values().length);
    }

    public void setFace(BalloonFace face) {
        this.setFaceInt(face.ordinal());
    }

    public BalloonFace getFace() {
        return BalloonFace.values()[Mth.clamp(this.entityData.get(FACE), 0,  BalloonFace.values().length - 1)];
    }

    public float getRotZ() {
        return Mth.clamp(this.entityData.get(ROT_Z), -20, 20);
    }

    public void setRotZ(float rot) {
        this.entityData.set(ROT_Z, rot);
    }

    public void setBalloonColor(int color) {
        this.entityData.set(BALLOON_COLOR, color);
    }

    public int getBalloonColor() {
        return this.entityData.get(BALLOON_COLOR);
    }

    public float getPopProgress(float partialTick) {
        if(deathTime == 0){
            return 0;
        }
        return  ((float)deathTime + partialTick - 1.0F) / 3.0F;
    }
    protected SoundEvent getHurtSound(DamageSource source) {
        return CSSoundRegistry.BALLOON_HURT.get();
    }

    protected void tickDeath() {
        int max = 3;
        if(this.deathTime == 0){
            if(!this.isSilent()){
                this.playSound(CSSoundRegistry.BALLOON_POP.get(), this.getSoundVolume(), 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.gameEvent(GameEvent.ENTITY_ROAR);
            }
        }
        ++this.deathTime;
        if (this.deathTime == max && !this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, (byte) 67);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 67) {
            int color = this.getBalloonColor();
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            for (int i = 0; i < 5 + random.nextInt(2) + 5; i++) {
                this.level.addParticle(CSParticleRegistry.BALLOON_SHARD.get(), this.getX(), this.getY(0.5F), this.getZ(), r, g, b);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }
    public void setHandGesture(GloveGesture gesture){
        if(this.getChild() instanceof BadloonHandEntity hand){
            hand.setGesture(gesture);
        }
    }

    public boolean hurt(DamageSource source, float f) {
        if(source == DamageSource.CACTUS || source.isProjectile()){
            f = 100;
        }
        return super.hurt(source, f);
    }

    @Override
    protected void dropAllDeathLoot(DamageSource source) {
        Entity entity = source.getEntity();

        int i = net.minecraftforge.common.ForgeHooks.getLootingLevel(this, entity, source);
        this.captureDrops(new java.util.ArrayList<>());

        boolean flag = this.lastHurtByPlayerTime > 0;
        if (this.shouldDropLoot() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.dropFromLootTable(source, flag);
            this.dropCustomDeathLoot(source, i, flag);
        }
        if(dropMusicDisk){
            dropMusicDisk = false;
            this.spawnAtLocation(CSItemRegistry.MUSIC_DISC_DRIFT.get());
        }
        this.dropEquipment();
        this.dropExperience();

        Collection<ItemEntity> drops = captureDrops(null);
        Collection<ItemEntity> splitDrops = new ArrayList<>();
        for(ItemEntity drop : drops){
            for(int stackSize = 0; stackSize < drop.getItem().getCount(); stackSize++){
                ItemStack single = drop.getItem().copy();
                single.setCount(1);
                ItemEntity split = new ItemEntity(level, getX(), getEyeY(), getZ(), single);
                split.setDefaultPickUpDelay();
                splitDrops.add(split);
            }
        }
        if (!net.minecraftforge.common.ForgeHooks.onLivingDrops(this, source, splitDrops, i, lastHurtByPlayerTime > 0))
            splitDrops.forEach(e -> popItem(e, splitDrops.size()));
    }

    private void popItem(ItemEntity e, int count) {
        float radius = 1.0F + random.nextFloat();
        Random seeded = new Random(this.getId());
        float renderYawOffset = this.getYRot() + seeded.nextInt(360) + droppedItems / (float)count * 360F;
        float angle = (float) ((0.01745329251F * renderYawOffset) + 3.15);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        Vec3 vec = new Vec3(extraX, 0.8F, extraZ).normalize().scale(0.2F);
        e.setDeltaMovement(vec);
        droppedItems++;
        level.addFreshEntity(e);
    }



    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setBalloonColor(BalloonItem.DEFAULT_COLOR);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return true;
    }

    public static boolean canBadloonSpawn(EntityType<BadloonEntity> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return reason == MobSpawnType.SPAWNER || random.nextFloat() < 0.2F && iServerWorld.canSeeSky(pos);
    }
}
