package com.github.alexthe668.cloudstorage.entity;

import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.google.common.collect.Lists;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BalloonCargoEntity extends Entity {

    protected static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData.defineId(BalloonCargoEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Optional<UUID>> BALLOON_UUID = SynchedEntityData.defineId(BalloonCargoEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID = SynchedEntityData.defineId(BalloonCargoEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> BALLOON_ID = SynchedEntityData.defineId(BalloonCargoEntity.class, EntityDataSerializers.INT);
    public int time;
    public boolean dropItem = true;
    @Nullable
    public CompoundTag blockData;
    private BlockState blockState = Blocks.SAND.defaultBlockState();
    private boolean hurtEntities;
    private int fallDamageMax = 40;
    private float fallDamagePerDistance;
    private int containerSize;
    private List<ItemStack> containerItems = new ArrayList<>();
    public BalloonCargoEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public BalloonCargoEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(CSEntityRegistry.BALLOON_CARGO.get(), world);
    }

    private BalloonCargoEntity(Level p_31953_, double p_31954_, double p_31955_, double p_31956_, BlockState p_31957_) {
        this(CSEntityRegistry.BALLOON_CARGO.get(), p_31953_);
        this.blockState = p_31957_;
        this.blocksBuilding = true;
        this.setPos(p_31954_, p_31955_, p_31956_);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = p_31954_;
        this.yo = p_31955_;
        this.zo = p_31956_;
        this.setStartPos(this.blockPosition());
    }

    public static BalloonCargoEntity createCargo(Level level, BlockPos pos, BlockState state, @Nullable CompoundTag tag) {
        BalloonCargoEntity fallingblockentity = new BalloonCargoEntity(level, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)) : state);
        fallingblockentity.blockData = tag;
        level.addFreshEntity(fallingblockentity);
        return fallingblockentity;
    }

    public boolean isAttackable() {
        return false;
    }

    public BlockPos getStartPos() {
        return this.entityData.get(DATA_START_POS);
    }

    public void setStartPos(BlockPos p_31960_) {
        this.entityData.set(DATA_START_POS, p_31960_);
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    public void push(Entity entity) {
    }

    public void tick() {
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.move(MoverType.SELF, this.getDeltaMovement());
        Entity balloon = this.getBalloon();
        if (!level.isClientSide) {
            if(balloon == null || !balloon.isAlive()){
                FallingBlockEntity fallingblockentity = new FallingBlockEntity(level, this.getX(), this.getY(), this.getZ(), this.blockState);
                fallingblockentity.blockData = this.blockData;
                level.addFreshEntity(fallingblockentity);
                this.remove(RemovalReason.DISCARDED);
            }else if(balloon instanceof BalloonEntity){
                float length = ((BalloonEntity)balloon).getStringLength();
                if (this.distanceTo(balloon) > length) {
                    Vec3 back = balloon.position().add(0, -  length - 1F, 0).subtract(this.position());
                    this.setDeltaMovement(this.getDeltaMovement().add(back.scale(0.08F)));
                }
            }
        }
    }

    public boolean displayFireAnimation() {
        return false;
    }

    public void fillCrashReportCategory(CrashReportCategory p_31962_) {
        super.fillCrashReportCategory(p_31962_);
        p_31962_.setDetail("Immitating BlockState", this.blockState.toString());
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_149654_) {
        super.recreateFromPacket(p_149654_);
        this.blockState = Block.stateById(p_149654_.getData());
        this.blocksBuilding = true;
        double d0 = p_149654_.getX();
        double d1 = p_149654_.getY();
        double d2 = p_149654_.getZ();
        this.setPos(d0, d1, d2);
        this.setStartPos(this.blockPosition());
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.getBlockState()));
    }

    public boolean isPushable() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_START_POS, BlockPos.ZERO);
        this.entityData.define(PLAYER_UUID, Optional.empty());
        this.entityData.define(BALLOON_UUID, Optional.empty());
        this.entityData.define(BALLOON_ID, -1);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    public Entity getBalloon() {
        UUID id = getBalloonUUID();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    @Nullable
    public UUID getBalloonUUID() {
        return this.entityData.get(BALLOON_UUID).orElse(null);
    }

    public void setBalloonUUID(@Nullable UUID uniqueId) {
        this.entityData.set(BALLOON_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getPlayerUUID() {
        return this.entityData.get(PLAYER_UUID).orElse(null);
    }

    public void setPlayerUUID(@Nullable UUID uniqueId) {
        this.entityData.set(PLAYER_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getBalloonForRendering() {
        return this.level.getEntity(this.entityData.get(BALLOON_ID));
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        return InteractionResult.PASS;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("BalloonUUID")) {
            this.setBalloonUUID(compound.getUUID("BalloonUUID"));
        }
        if (compound.hasUUID("PlayerUUID")) {
            this.setBalloonUUID(compound.getUUID("PlayerUUID"));
        }
        this.blockState = NbtUtils.readBlockState(compound.getCompound("BlockState"));
        this.time = compound.getInt("Time");
        if (compound.contains("HurtEntities", 99)) {
            this.hurtEntities = compound.getBoolean("HurtEntities");
            this.fallDamagePerDistance = compound.getFloat("FallHurtAmount");
            this.fallDamageMax = compound.getInt("FallHurtMax");
        } else if (this.blockState.is(BlockTags.ANVIL)) {
            this.hurtEntities = true;
        }

        if (compound.contains("DropItem", 99)) {
            this.dropItem = compound.getBoolean("DropItem");
        }

        if (compound.contains("TileEntityData", 10)) {
            this.blockData = compound.getCompound("TileEntityData");
        }

        if (this.blockState.isAir()) {
            this.blockState = Blocks.CHEST.defaultBlockState();
        }
        this.containerSize = compound.getInt("ContainerSize");
        ListTag tag = compound.getList("ContainerItems", 10);
        for(int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundtag = tag.getCompound(i);
            ItemStack itemstack = ItemStack.of(compoundtag);
            this.containerItems.add(itemstack);
        }

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.getBalloonUUID() != null) {
            compound.putUUID("BalloonUUID", this.getBalloonUUID());
        }
        if (this.getPlayerUUID() != null) {
            compound.putUUID("PlayerUUID", this.getPlayerUUID());
        }
        compound.put("BlockState", NbtUtils.writeBlockState(this.blockState));
        compound.putInt("Time", this.time);
        compound.putBoolean("DropItem", this.dropItem);
        compound.putBoolean("HurtEntities", this.hurtEntities);
        compound.putFloat("FallHurtAmount", this.fallDamagePerDistance);
        compound.putInt("FallHurtMax", this.fallDamageMax);
        if (this.blockData != null) {
            compound.put("TileEntityData", this.blockData);
        }
        compound.putInt("ContainerSize", this.containerSize);
        ListTag tag = new ListTag();
        for(int i = 0; i < containerItems.size(); ++i) {
            if (!containerItems.get(i).isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                containerItems.get(i).save(compoundtag);
                tag.add(compoundtag);
            }
        }
        compound.put("ContainerItems", tag);
    }

    public void copyContainerData(Container container) {
        this.containerSize = container.getContainerSize();
        for(int i = 0; i < this.containerSize; i++){
            ItemStack at = container.getItem(i);
            if(!at.isEmpty()){
                containerItems.add(at);
            }
        }
    }

    public List<ItemStack> getContainerItems(){
        return containerItems;
    }

    public int getContainerSize(){
        return containerSize;
    }
}
