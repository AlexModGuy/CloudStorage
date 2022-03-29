package com.github.alexthe668.cloudstorage.entity;

import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.entity.ai.BloviatorAttackGoal;
import com.github.alexthe668.cloudstorage.entity.ai.FlightMoveController;
import com.github.alexthe668.cloudstorage.entity.ai.FlyAroundGoal;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class BloviatorEntity extends Monster {

    private static final EntityDataAccessor<Float> CLOUD_SCALE = SynchedEntityData.defineId(BloviatorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> PUSH_ENTITY = SynchedEntityData.defineId(BloviatorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SHOCK_ENTITY = SynchedEntityData.defineId(BloviatorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> THUNDERY = SynchedEntityData.defineId(BloviatorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CHARGE = SynchedEntityData.defineId(BloviatorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SHOCKTIME = SynchedEntityData.defineId(BloviatorEntity.class, EntityDataSerializers.INT);
    private static final ResourceLocation THUNDERY_LOOT_TABLE = new ResourceLocation("cloudstorage:entities/bloviator_thunder");
    public final double[][] positions = new double[64][4];
    public int posPointer = -1;
    private float pushProgress;
    private float prevPushProgress;
    private float transformProgress;
    private float prevTransformProgress;
    private int prevShockTime;
    private int prevChargeTime;

    protected BloviatorEntity(EntityType type, Level level) {
        super(type, level);
        this.moveControl = new FlightMoveController(this, 1F, true, 5F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.15F).add(Attributes.FLYING_SPEED, 0.15F).add(Attributes.FOLLOW_RANGE, 64D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLOUD_SCALE, 1F);
        this.entityData.define(PUSH_ENTITY, -1);
        this.entityData.define(SHOCK_ENTITY, -1);
        this.entityData.define(THUNDERY, false);
        this.entityData.define(CHARGE, 1);
        this.entityData.define(SHOCKTIME, 0);
    }

    public boolean isPushing() {
        return this.entityData.get(PUSH_ENTITY) != -1 && !this.isThundery();
    }

    public boolean isShocking() {
        return this.entityData.get(SHOCK_ENTITY) != -1 && this.isThundery();
    }

    public boolean isThundery() {
        return this.entityData.get(THUNDERY);
    }

    public void setThundery(boolean thundery, boolean progress) {
        this.entityData.set(THUNDERY, thundery);
        if (progress && thundery) {
            this.transformProgress = 5F;
            this.prevTransformProgress = 5F;
        }
    }

    @Nullable
    public Entity getPushingEntity() {
        if (!this.isPushing()) {
            return null;
        } else {
            return this.level.getEntity(this.entityData.get(PUSH_ENTITY));
        }
    }

    @Nullable
    public Entity getShockingEntity() {
        if (!this.isShocking()) {
            return null;
        } else {
            return this.level.getEntity(this.entityData.get(SHOCK_ENTITY));
        }
    }

    public int getChargeTime() {
        return this.entityData.get(CHARGE);
    }

    public void setChargeTime(int i) {
        this.entityData.set(CHARGE, i);
    }

    public int getShockTime() {
        return this.entityData.get(SHOCKTIME);
    }

    public void setShockTime(int i) {
        this.entityData.set(SHOCKTIME, i);
    }

    protected void setCloudScale(float size, boolean heal) {
        float i = Mth.clamp(size, 0.25F, 2F);
        this.entityData.set(CLOUD_SCALE, i);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(i * 16.0F);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(i);
        if (heal) {
            this.setHealth(this.getMaxHealth());
        }
        this.xpReward = (int) (i * 4);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public float getCloudScale() {
        return this.entityData.get(CLOUD_SCALE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33609_) {
        if (CLOUD_SCALE.equals(p_33609_)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }
        super.onSyncedDataUpdated(p_33609_);
    }

    protected ResourceLocation getDefaultLootTable() {
        return this.isTiny() ? this.isThundery() ? THUNDERY_LOOT_TABLE : this.getType().getDefaultLootTable() : BuiltInLootTables.EMPTY;
    }

    public void remove(Entity.RemovalReason reason) {
        float i = this.getCloudScale();
        if (!this.level.isClientSide && i > 0.25F && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = i / 4.0F;
            float j = i / 2F;
            int k = 2 + this.random.nextInt(3);

            for (int l = 0; l < k; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (l / 2) - 0.5F) * f;
                BloviatorEntity mini = CSEntityRegistry.BLOVIATOR.get().create(this.level);
                if (this.isPersistenceRequired()) {
                    mini.setPersistenceRequired();
                }
                mini.setCustomName(component);
                mini.setNoAi(flag);
                mini.setInvulnerable(this.isInvulnerable());
                mini.setCloudScale(j, true);
                mini.setThundery(this.isThundery(), true);
                mini.moveTo(this.getX() + (double) f1, this.getY(0.5D), this.getZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                this.level.addFreshEntity(mini);
            }
        }

        super.remove(reason);
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
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
        this.goalSelector.addGoal(1, new BloviatorAttackGoal(this));
        this.goalSelector.addGoal(2, new FlyAroundGoal(this, 15, 7, 20, 1.0F));
        this.targetSelector.addGoal(1, (new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, null)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Sheep.class, 10, false, false, null)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        fallDistance = 0;
    }

    public void tick() {
        super.tick();
        this.prevPushProgress = pushProgress;
        this.prevTransformProgress = transformProgress;
        this.prevShockTime = getShockTime();
        this.prevChargeTime = getChargeTime();
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.8F, 0.6F, 0.8F));
        if (this.posPointer < 0) {
            for (int i = 0; i < this.positions.length; ++i) {
                this.positions[i][0] = this.getX();
                this.positions[i][1] = this.getY();
                this.positions[i][2] = this.getZ();
                this.positions[i][3] = this.yBodyRot;
            }
        }

        if (++this.posPointer == this.positions.length) {
            this.posPointer = 0;
        }

        this.positions[this.posPointer][0] = this.getX();
        this.positions[this.posPointer][1] = this.getY();
        this.positions[this.posPointer][2] = this.getZ();
        this.positions[this.posPointer][3] = this.yBodyRot;

        Entity pushing = this.getPushingEntity();
        if (pushing == null) {
            if (this.pushProgress > 0.0F) {
                this.pushProgress -= 1.0F;
            }
            if (!level.isClientSide && canPush(this.getTarget())) {
                this.entityData.set(PUSH_ENTITY, this.getTarget().getId());
            }
        } else {
            if (this.pushProgress < 5.0F) {
                this.pushProgress += 1.0F;
            }
            if (canPush(pushing)) {
                Vec3 mouth = this.getMouthVec(1.0F);
                Vec3 vec2 = pushing.position().subtract(this.position()).normalize().scale(0.1F * this.getCloudScale());
                if (level.isClientSide) {
                    for (int i = 0; i < this.getCloudCount(); i++) {
                        Vec3 randomOffset = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).scale(this.getCloudScale());
                        Vec3 vec3 = pushing.getEyePosition().add(randomOffset).subtract(mouth).normalize().add(this.getDeltaMovement()).scale(0.5F);
                        this.level.addParticle(CSParticleRegistry.BLOVIATOR_BREATH, mouth.x, mouth.y, mouth.z, vec3.x, vec3.y, vec3.z);
                    }
                }
                pushing.setDeltaMovement(pushing.getDeltaMovement().add(vec2));
            } else if (!level.isClientSide) {
                this.entityData.set(PUSH_ENTITY, -1);
            }
        }
        if (this.isThundery()) {
            if (this.transformProgress < 5.0F) {
                this.transformProgress++;
            }

            if (this.getTarget() != null && this.getTarget().isAlive() && this.hasLineOfSight(this.getTarget())) {
                double d0 = this.getX() - this.getTarget().getX();
                double d2 = this.getZ() - this.getTarget().getZ();
                double xzDist = Math.sqrt(d0 * d0 + d2 * d2);
                if (this.isShocking()) {
                    if (this.getShockTime() > 0) {
                        this.setShockTime(this.getShockTime() - 1);


                    } else {
                        if (this.getShockingEntity() != null) {
                            this.shock(this.getShockingEntity());
                        }
                        this.entityData.set(SHOCK_ENTITY, -1);
                    }
                } else {
                    if (this.getChargeTime() < this.getMaxChargeTime()) {
                        this.setChargeTime(this.getChargeTime() + 1);
                    }
                }
                if (this.getY() > this.getTarget().getY(0.9D) && xzDist < this.getCloudScale() * 2 && this.getChargeTime() == this.getMaxChargeTime()) {
                    this.setChargeTime(0);
                    this.entityData.set(SHOCK_ENTITY, this.getTarget().getId());
                    this.setShockTime(5);
                }
            }
            if (this.getChargeTime() > 1) {
                double d0 = (random.nextFloat() - 0.5F) * this.getBbWidth() + this.getDeltaMovement().x;
                double d1 = (random.nextFloat() - 0.5F) * this.getBbHeight() + this.getDeltaMovement().y;
                double d2 = (random.nextFloat() - 0.5F) * this.getBbWidth() + this.getDeltaMovement().z;
                double dist = 0.2F + random.nextFloat() * 0.2F;
                double d3 = d0 * dist;
                double d4 = d1 * dist;
                double d5 = d2 * dist;
                this.level.addParticle(CSParticleRegistry.STATIC_LIGHTNING, this.getX() + d0, this.getY() + d1, this.getZ() + d2, d3, d4, d5);
            }
        } else {
            if (this.transformProgress > 5.0F) {
                this.transformProgress--;
            }
            if ((this.level.isRaining() || this.level.isThundering()) && this.level.canSeeSky(this.blockPosition())) {
                this.setThundery(true, false);
            }
        }
    }

    private void shock(Entity target) {
        float damage = Mth.clamp(this.getCloudCount() * 0.75F, 1, 10);
        LightningBolt dummy = EntityType.LIGHTNING_BOLT.create(level);
        dummy.setDamage(damage);
        if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(target, dummy)) {
            target.thunderHit((ServerLevel) this.level, dummy);
        }
        if (target instanceof LivingEntity living) {
            living.setLastHurtByMob(this);
        }
    }

    public int getMaxChargeTime() {
        return Math.max((int) (20 * this.getCloudScale()), 10);
    }

    public boolean canPush(Entity entity) {
        return entity != null && entity.isAlive() && entity.fallDistance < 3.0F && this.hasLineOfSight(entity) && this.distanceTo(entity) < 16 * this.getCloudScale();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Size", this.getCloudScale());
        tag.putBoolean("Thunder", this.isThundery());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        this.setCloudScale(tag.getInt("Size"), false);
        this.setThundery(tag.getBoolean("Thunder"), true);
        super.readAdditionalSaveData(tag);
    }

    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getCloudScale());
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
        float rand = random.nextFloat();
        float scale = 1F;
        if (rand < 0.001F) {
            scale = 2F;
        } else if (rand < 0.5F) {
            scale = 0.5F;
        }
        this.setCloudScale(scale, true);
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    public boolean isTiny() {
        return this.getCloudScale() <= 0.25F;
    }

    public double getLatencyVar(int pointer, int index, float partialTick) {
        if (this.isDeadOrDying()) {
            partialTick = 1.0F;
        }
        int i = this.posPointer - pointer & 63;
        int j = this.posPointer - pointer - 1 & 63;
        double d0 = this.positions[j][index];
        double d1 = Mth.wrapDegrees(this.positions[i][index] - d0);
        return d0 + d1 * partialTick;
    }

    public int getMaxHeadYRot() {
        return 4;
    }

    public int getHeadRotSpeed() {
        return 4;
    }

    public int getCloudCount() {
        float f = this.getCloudScale();
        if (f > 1F) {
            return 7;
        } else if (f > 0.5F) {
            return 5;
        } else if (f > 0.25) {
            return 3;
        } else {
            return 1;
        }
    }

    public Vec3 getMouthVec(float partialTicks) {
        double x = Mth.lerp(partialTicks, this.xOld, this.getX());
        double y = Mth.lerp(partialTicks, this.yOld, this.getY());
        double z = Mth.lerp(partialTicks, this.zOld, this.getZ());
        Vec3 pos = new Vec3(x, y, z);
        Vec3 offset = new Vec3(0, 0.35F, 1.15F).scale(this.getCloudScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
        return pos.add(offset);
    }


    public float getPushProgress(float partialTick) {
        return (prevPushProgress + (pushProgress - prevPushProgress) * partialTick) / 5F;
    }


    public float getTransformProgress(float partialTick) {
        return (prevTransformProgress + (transformProgress - prevTransformProgress) * partialTick) / 5F;
    }

    public float getShockTimeLerp(float partialTick) {
        return prevShockTime + (getShockTime() - prevShockTime) * partialTick;
    }

    public float getChargeTimeLerp(float partialTick) {
        return prevChargeTime + (getChargeTime() - prevChargeTime) * partialTick;
    }


    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
        if(this.isTiny() && itemstack.is(Items.GLASS_BOTTLE)){
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            ItemStack bottle = new ItemStack(CSItemRegistry.ANGRY_CLOUD_IN_A_BOTTLE.get());
            if(!player.addItem(bottle)){
                player.spawnAtLocation(bottle);
            }
            this.remove(RemovalReason.DISCARDED);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
