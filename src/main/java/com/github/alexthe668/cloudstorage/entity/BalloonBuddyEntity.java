package com.github.alexthe668.cloudstorage.entity;

import com.github.alexthe666.citadel.server.entity.IComandableMob;
import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.entity.ai.BalloonBuddyAttackGoal;
import com.github.alexthe668.cloudstorage.entity.ai.BalloonBuddyFollowGoal;
import com.github.alexthe668.cloudstorage.entity.ai.FlightMoveController;
import com.github.alexthe668.cloudstorage.entity.ai.FlyAroundGoal;
import com.github.alexthe668.cloudstorage.item.BalloonItem;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.github.alexthe668.cloudstorage.misc.CSSoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class BalloonBuddyEntity extends TamableAnimal implements LivingBalloon, BalloonFlyer, IComandableMob {

    private static final EntityDataAccessor<Float> ROT_Z = SynchedEntityData.defineId(BalloonBuddyEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(BalloonBuddyEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(BalloonBuddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FACE = SynchedEntityData.defineId(BalloonBuddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PERSONALITY = SynchedEntityData.defineId(BalloonBuddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BALLOON_COLOR = SynchedEntityData.defineId(BalloonBuddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(BalloonBuddyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ABILITYTIME = SynchedEntityData.defineId(BalloonBuddyEntity.class, EntityDataSerializers.INT);
    public int fearOfBeingPoppedCooldown = 0;
    public float prevRotZ;
    private Vec3 randomMoveOffset = null;
    private float prevAbilityProgress;
    private float abilityProgress;
    private int coolnessCooldown = 0;

    protected BalloonBuddyEntity(EntityType type, Level level) {
        super(type, level);
        this.xpReward = 1;
        this.moveControl = new FlightMoveController(this, 1F, true);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_CACTUS, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_CACTUS, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.15F).add(Attributes.FLYING_SPEED, 0.15F).add(Attributes.FOLLOW_RANGE, 32D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BalloonBuddyAttackGoal(this));
        this.goalSelector.addGoal(2, new BalloonBuddyFollowGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new FlyAroundGoal(this, 6, 3, 40, 1.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        this.prevRotZ = this.getRotZ();
        this.prevAbilityProgress = this.abilityProgress;
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.8F, 0.6F, 0.8F));
        if (!level.isClientSide) {
            Entity child = getChild();
            if (child == null) {
                BadloonHandEntity hand = new BadloonHandEntity(this);
                hand.setPos(this.position().add(0, 0.5F, 0));
                level.addFreshEntity(hand);
                this.setChildId(hand.getUUID());
                this.entityData.set(CHILD_ID, hand.getId());
            } else {
                this.entityData.set(CHILD_ID, child.getId());
                if (this.distanceTo(child) > 4) {
                    Vec3 back = child.position().subtract(this.position()).normalize();
                    this.setDeltaMovement(this.getDeltaMovement().add(back.scale(0.1F)));
                }
            }
            if (randomMoveOffset == null || random.nextInt(5) == 0) {
                randomMoveOffset = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            }
            if (this.isInSittingPose()) {
                Vec3 add = new Vec3(0.0F, 0.02F * Math.sin(0.1F * tickCount), 0.0F);
                this.setDeltaMovement(this.getDeltaMovement().add(add));
            } else if (randomMoveOffset != null) {
                Vec3 add = randomMoveOffset.normalize().scale(0.01F + random.nextFloat() * 0.01F);
                this.setDeltaMovement(this.getDeltaMovement().add(add));
            }
            if (this.isOnGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.08, 0));
            }
            if (fearOfBeingPoppedCooldown > 0) {
                fearOfBeingPoppedCooldown--;
            }
        }
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float) (vector3d.x * vector3d.x + vector3d.z * vector3d.z));
        if (!level.isClientSide) {
            float xRotTarget = (float) (Mth.atan2(vector3d.y, f) * 0.05F * (double) (180F / (float) Math.PI));
            this.setXRot(BalloonFace.rotlerp(this.getXRot(), xRotTarget, 5F));
        }
        if (this.getRotZ() > 0) {
            this.setRotZ(Math.max(this.getRotZ() - 3, 0));
        } else if (this.getRotZ() < 0) {
            this.setRotZ(Math.min(this.getRotZ() + 3, 0));
        }
        if (this.getAbilityTime() > 0) {
            this.setAbilityTime(this.getAbilityTime() - 1);
            if (abilityProgress < 10F) {
                this.abilityProgress++;
            }
            if (this.getPersonality() == BalloonFace.SCARY) {
                List<Monster> list = this.level.getEntitiesOfClass(Monster.class, this.getBoundingBox().inflate(16, 8, 16));
                for (Monster e : list) {
                    e.setTarget(null);
                    e.setLastHurtByMob(null);
                    if (random.nextInt(5) == 0) {
                        Vec3 vec = LandRandomPos.getPosAway(e, 20, 7, this.position());
                        if (vec != null) {
                            e.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.5D);
                        }
                    }
                }
            }
            if (this.getPersonality() == BalloonFace.TROLL) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.08, 0));
                if (this.getAbilityTime() < 2) {
                    if (this.getChild() instanceof BadloonHandEntity hand) {
                        hand.ejectPassengers();
                    }
                }
            }
        }
        if (this.getAbilityTime() == 0 && this.abilityProgress > 0) {
            this.abilityProgress--;
        }
        if (!this.level.isClientSide && this.getPersonality() == BalloonFace.CHARMING && this.getAbilityTime() == 0 && (this.tickCount + this.getId()) % 100 == 0) {
            AABB aabb = this.getBoundingBox().inflate(16, 8, 16);
            Predicate<Entity> breedableAnimal = (animal) -> !(animal instanceof BalloonBuddyEntity) && animal instanceof Animal && !((Animal) animal).isBaby() && ((Animal) animal).getInLoveTime() == 0 && (((Animal) animal).getBreedOffspring((ServerLevel) level, ((Animal) animal))) != null;
            List<Animal> list = this.level.getEntitiesOfClass(Animal.class, aabb, EntitySelector.NO_SPECTATORS.and(breedableAnimal));
            Map<EntityType, List<Animal>> typeAnimalMap = new HashMap<>();
            for (Animal animal : list) {
                if (typeAnimalMap.get(animal.getType()) == null) {
                    List<Animal> single = new ArrayList<>();
                    single.add(animal);
                    typeAnimalMap.put(animal.getType(), single);
                } else {
                    typeAnimalMap.get(animal.getType()).add(animal);
                }
            }
            List<Animal> closestToBreed = null;
            for (Map.Entry<EntityType, List<Animal>> entry : typeAnimalMap.entrySet()) {
                if (entry.getValue().size() >= 2) {
                    if (closestToBreed == null || entry.getValue().get(0).distanceTo(this) < closestToBreed.get(0).distanceTo(this)) {
                        closestToBreed = entry.getValue();
                    }
                }
            }
            boolean flag = false;
            if (closestToBreed != null) {
                for (Animal animal : closestToBreed) {
                    animal.setInLoveTime(600);
                    flag = true;
                }
            }
            this.setAbilityTime((flag ? 8000 : 200) + random.nextInt(200));
        }
        if (this.getPersonality() == BalloonFace.COOL && !level.isClientSide) {
            boolean flag = false;
            if (this.getTarget() != null && this.hasLineOfSight(this.getTarget())) {
                if (this.getAbilityTime() == 0) {
                    if (coolnessCooldown == 0) {
                        this.setAbilityTime(30);
                    }
                } else {
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 255, true, false, true));
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.CONFUSION, 30, 0, true, false, true));
                    if(!(this.getTarget() instanceof Player)){
                        this.getTarget().lookAt(EntityAnchorArgument.Anchor.EYES, this.position());
                    }
                    coolnessCooldown = 40 + random.nextInt(40);
                    flag = true;
                    this.lookAt(this.getTarget(), 360, 360);
                    this.level.broadcastEntityEvent(this, (byte) 69);
                    this.getTarget().setLastHurtByMob(null);
                    if(this.getTarget() instanceof Mob mob){
                        mob.setTarget(null);
                    }
                }
            }
            this.setHandGesture(flag ? GloveGesture.POINT : GloveGesture.IDLE);
            if (this.coolnessCooldown > 0) {
                this.coolnessCooldown--;
            }
        }
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public float getRotZ() {
        return Mth.clamp(this.entityData.get(ROT_Z), -20, 20);
    }

    public void setRotZ(float rot) {
        this.entityData.set(ROT_Z, rot);
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHILD_UUID, Optional.empty());
        this.entityData.define(CHILD_ID, -1);
        this.entityData.define(ROT_Z, 0F);
        this.entityData.define(FACE, 0);
        this.entityData.define(PERSONALITY, 0);
        this.entityData.define(BALLOON_COLOR, BalloonItem.DEFAULT_COLOR);
        this.entityData.define(COMMAND, 0);
        this.entityData.define(ABILITYTIME, 0);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        fallDistance = 0;
    }

    @javax.annotation.Nullable
    public UUID getChildId() {
        return this.entityData.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@javax.annotation.Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getHandForRendering() {
        return this.level.getEntity(this.entityData.get(CHILD_ID));
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

    private void setFaceInt(int face) {
        this.entityData.set(FACE, face % BalloonFace.values().length);
    }

    private BalloonFace getFaceFromData() {
        return BalloonFace.values()[Mth.clamp(this.entityData.get(FACE), 0, BalloonFace.values().length - 1)];
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return CSSoundRegistry.BALLOON_HURT.get();
    }

    public BalloonFace getFace() {
        if (getFaceFromData() != BalloonFace.NEUTRAL) {
            return getFaceFromData();
        } else {
            return getPersonality();
        }
    }

    public void setFace(BalloonFace face) {
        this.setFaceInt(face.ordinal());
    }

    private void setPersonalityInt(int face) {
        this.entityData.set(PERSONALITY, face % BalloonFace.values().length);
    }

    public BalloonFace getPersonality() {
        return BalloonFace.values()[Mth.clamp(this.entityData.get(PERSONALITY), 0, BalloonFace.values().length - 1)];
    }

    public void setPersonality(BalloonFace face) {
        this.setPersonalityInt(face.ordinal());
    }

    public int getBalloonColor() {
        return this.entityData.get(BALLOON_COLOR);
    }

    public void setBalloonColor(int color) {
        this.entityData.set(BALLOON_COLOR, color);
    }

    public int getAbilityTime() {
        return this.entityData.get(ABILITYTIME);
    }

    public void setAbilityTime(int time) {
        this.entityData.set(ABILITYTIME, time);
    }

    public float getPopProgress(float partialTick) {
        if (deathTime == 0) {
            return 0;
        }
        return ((float) deathTime + partialTick - 1.0F) / 3.0F;
    }

    protected void tickDeath() {
        int max = 3;
        if(this.deathTime == 0){
            if(!this.isSilent()){
                this.playSound(CSSoundRegistry.BALLOON_POP.get(), this.getSoundVolume(), 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
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
        } else if (id == 68) {
            for (int i = 0; i < 5 + random.nextInt(2) + 2; i++) {
                this.level.addParticle(CSParticleRegistry.STOP_SPAWN.get(), this.getRandomX(1.0F), this.getY(0.5F), this.getRandomZ(1.0F), 0, 0, 0);
            }
        } else if (id == 69) {
            this.level.addParticle(CSParticleRegistry.COOL.get(), this.getRandomX(0.8F), this.getY(0.75F), this.getRandomZ(0.8F), 0, 0, 0);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public boolean hurt(DamageSource source, float f) {
        if (source == DamageSource.CACTUS || source.isProjectile()) {
            f = 100;
        }
        return super.hurt(source, f);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ChildUUID")) {
            this.setChildId(compound.getUUID("ChildUUID"));
        }
        this.setPersonalityInt(compound.getInt("Personality"));
        this.setFaceInt(compound.getInt("Face"));
        this.setBalloonColor(compound.getInt("BalloonColor"));
        this.setCommand(compound.getInt("BalloonCommand"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getChildId() != null) {
            compound.putUUID("ChildUUID", this.getChildId());
        }
        compound.putInt("Face", this.getFace().ordinal());
        compound.putInt("Personality", this.getPersonality().ordinal());
        compound.putInt("BalloonColor", this.getBalloonColor());
        compound.putInt("BalloonCommand", this.getCommand());
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int i) {
        this.entityData.set(COMMAND, i);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(itemstack.is(Items.STRING) && this.getHealth() < this.getMaxHealth()){
            this.heal(6);
        }else if (isOwnedBy(player)) {
            if(player.isShiftKeyDown() && itemstack.isEmpty()){
                ItemStack balloonStack = turnIntoItem();
                if(!player.addItem(balloonStack)){
                    this.spawnAtLocation(balloonStack);
                }
                this.remove(RemovalReason.DISCARDED);
            }else{
                this.playerSetCommand(player, this);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void sendCommandMessage(Player owner, int command, Component name) {
        owner.displayClientMessage(Component.translatable("entity.balloon_buddy.command_" + command, this.getName()), true);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
        return null;
    }


    public boolean isInSittingPose() {
        return this.getCommand() == 1;
    }

    public void setInSittingPose(boolean sit) {
        if (sit) {
            this.setCommand(1);
        }
    }

    public boolean stopFlying() {
        return this.isInSittingPose() && !shouldFollow();
    }

    public boolean shouldFollow() {
        return this.getCommand() == 2 && (this.getPersonality() != BalloonFace.TROLL || !this.shouldFaceStopAttacking());
    }

    public void clearMovement() {
        if (this.getMoveControl() instanceof FlightMoveController moveHelper) {
            moveHelper.stop();
        }
    }

    public float getAbilityProgress(float partialTicks) {
        return (prevAbilityProgress + (abilityProgress - prevAbilityProgress) * partialTicks) / 10F;
    }

    public void setHandGesture(GloveGesture gesture) {
        if (this.getChild() instanceof BadloonHandEntity hand) {
            hand.setGesture(gesture);
        }
    }

    @Override
    public double getVisibilityPercent(@javax.annotation.Nullable Entity entity) {
        if (this.getPersonality() == BalloonFace.EYEPATCH) {
            return 0.0D;
        }
        return super.getVisibilityPercent(entity);
    }

    public boolean shouldFaceStopAttacking() {
        if (this.getPersonality() == BalloonFace.SCARY) {
            Entity owner = this.getOwner();
            return owner != null && this.distanceTo(owner) > 16D;
        }
        if (this.getPersonality() == BalloonFace.TROLL && this.getChild() instanceof BadloonHandEntity hand) {
            if (hand.isVehicle()) {
                return true;
            }
        }
        return !this.getPersonality().doesMeleeAttacks();
    }

    public boolean doesDealDamage() {
        return this.getPersonality() != BalloonFace.SCARY;
    }

    public float getAlpha(float partialTick) {
        return this.getPersonality() == BalloonFace.EYEPATCH ? 1.0F - 0.75F * this.getAbilityProgress(partialTick) : 1.0F;
    }

    public ItemStack turnIntoItem(){
        ItemStack stack = new ItemStack(CSItemRegistry.BALLOON_BUDDY.get());
        CompoundTag tag = new CompoundTag();
        CompoundTag mobNBT = new CompoundTag();
        CompoundTag display = new CompoundTag();
        this.addAdditionalSaveData(mobNBT);
        tag.put("MobNBT", mobNBT);
        tag.putInt("Personality", this.getPersonality().ordinal());
        if(this.getBalloonColor() != BalloonItem.DEFAULT_COLOR) {
            display.putInt("color", this.getBalloonColor());
            tag.put("display", display);
        }
        tag.putFloat("Health", this.getHealth());
        stack.setTag(tag);
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal) entityIn).isOwnedBy(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }
        return super.isAlliedTo(entityIn);
    }

    @javax.annotation.Nullable
    public ItemStack getPickResult() {
        return turnIntoItem();
    }
}
