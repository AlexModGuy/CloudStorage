package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.block.AbstractCloudChestBlockEntity;
import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.block.CloudChestBlockEntity;
import com.github.alexthe668.cloudstorage.client.render.BalloonTextures;
import com.github.alexthe668.cloudstorage.entity.BalloonCargoEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonTieEntity;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;

public class BalloonItem extends Item implements DyeableLeatherItem {

    private static final int DEFAULT_COLOR = 0XE72929;
    private static final int[] BALLOONCOLORS = new int[]{DEFAULT_COLOR};

    public BalloonItem() {
        super(new Item.Properties().stacksTo(8).tab(CloudStorage.TAB));
    }

    public static int getBalloonColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : DEFAULT_COLOR;
    }

    public static boolean isStatic(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.getBoolean("static");
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flags) {
        super.appendHoverText(stack, level, components, flags);
        if (isStatic(stack)) {
            components.add(new TranslatableComponent("item.cloudstorage.balloon.static").withStyle(ChatFormatting.AQUA));
        }
    }

    public boolean hasCustomColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) && compoundtag.getInt("color") != DEFAULT_COLOR;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
        consumer.accept((net.minecraftforge.client.IItemRenderProperties) CloudStorage.PROXY.getISTERProperties(false));
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowdedIn(tab)) {
            for (int i = 0; i < BALLOONCOLORS.length; i++) {
                list.add(createBalloon(DEFAULT_COLOR, false));
            }
            for (int i = 0; i < BALLOONCOLORS.length; i++) {
                list.add(createBalloon(DEFAULT_COLOR, true));
            }
        }
    }

    private ItemStack createBalloon(int color, boolean lgihtning) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("static", lgihtning);
        tag.putInt("color", color);
        ItemStack stack = new ItemStack(this);
        stack.setTag(tag);
        return stack;
    }

    public int getColor(ItemStack stack) {
        return getBalloonColor(stack);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        boolean rightHand = false;
        boolean leftHand = false;
        if (entity instanceof LivingEntity) {
            LivingEntity player = (LivingEntity) entity;
            boolean flag = held || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == CSItemRegistry.BALLOON.get();
            if (flag) {
                if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == CSItemRegistry.BALLOON.get()) {
                    leftHand = leftHand || player.getMainArm() == HumanoidArm.LEFT;
                    rightHand = rightHand || player.getMainArm() == HumanoidArm.RIGHT;
                    flag = true;
                }
                if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == CSItemRegistry.BALLOON.get()) {
                    leftHand = leftHand || player.getMainArm() != HumanoidArm.LEFT;
                    rightHand = rightHand || player.getMainArm() != HumanoidArm.RIGHT;
                    flag = true;
                }
                if (flag) {
                    if (entity.getDeltaMovement().y < 0) {
                        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1F, 0.8F, 1F));
                    }
                    entity.fallDistance *= 0.9F;
                }
            }
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        Level level = context.getLevel();
        BlockState blockstate = level.getBlockState(blockpos);
        BlockPos blockpos1 = blockpos.relative(direction);
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (!isStatic(itemstack) && blockstate.is(CSBlockRegistry.STATIC_CLOUD.get())) {
            CompoundTag tag = itemstack.getOrCreateTag().copy();
            tag.putBoolean("static", true);
            ItemStack copyOff = itemstack.copy();
            copyOff.setCount(1);
            copyOff.setTag(tag);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            if (!player.addItem(copyOff)) {
                player.drop(copyOff, true);
            }
            level.setBlockAndUpdate(blockpos, CSBlockRegistry.CLOUD.get().defaultBlockState());
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else if (isStatic(itemstack) && blockstate.is(CSBlockRegistry.CLOUD.get())) {
            CompoundTag tag = itemstack.getOrCreateTag().copy();
            tag.putBoolean("static", false);
            ItemStack copyOff = itemstack.copy();
            copyOff.setCount(1);
            copyOff.setTag(tag);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            if (!player.addItem(copyOff)) {
                player.drop(copyOff, true);
            }
            level.setBlockAndUpdate(blockpos, CSBlockRegistry.STATIC_CLOUD.get().defaultBlockState());
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else if (blockstate.is(CSBlockRegistry.CLOUD_CHEST.get()) || blockstate.is(CSBlockRegistry.STATIC_CLOUD_CHEST.get())) {
            BlockEntity te = level.getBlockEntity(blockpos);
            if (te instanceof AbstractCloudChestBlockEntity cloudChest) {
                if (cloudChest.hasBalloonFor(player)) {
                    this.setColor(itemstack, cloudChest.getBalloonFor(player));
                    ItemEntity itemEntity = new ItemEntity(level, blockpos.getX() + 0.5F, blockpos.getY() + 0.75F, blockpos.getZ() + 0.5F, itemstack);
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
                cloudChest.setBalloonColorFor(player, this.getColor(itemstack));
                itemstack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        BalloonEntity balloon = CSEntityRegistry.BALLOON.get().create(level);

        CompoundTag compoundtag = itemstack.getTag();
        if (compoundtag != null) {
            EntityType.updateCustomEntityTag(level, player, balloon, compoundtag);
        }
        balloon.setBalloonColor(this.getColor(itemstack));
        balloon.setStringLength(BalloonEntity.DEFAULT_STRING_LENGTH);
        balloon.setCharged(isStatic(itemstack));
        balloon.setPos(blockpos1.getX() + 0.5F, blockpos1.getY() + 0.5F, blockpos1.getZ() + 0.5F);
        if (level.noCollision(balloon)) {
            if (blockstate.is(BlockTags.FENCES)) {
                BalloonTieEntity tie = BalloonTieEntity.getOrCreateKnot(level, blockpos);
                if (tie != null) {
                    tie.setBalloonCount(tie.getBalloonCount() + 1);
                    balloon.setChildId(tie.getUUID());
                }
            } else if (level.getBlockEntity(blockpos) instanceof Container) {
                BlockEntity te = level.getBlockEntity(blockpos);
                BalloonCargoEntity cargo = BalloonCargoEntity.createCargo(level, blockpos, blockstate, te.saveWithoutMetadata());
                cargo.copyContainerData(((Container) te));
                ((Container) te).clearContent();
                if (player != null) {
                    cargo.setPlayerUUID(player.getUUID());
                }
                level.removeBlockEntity(blockpos);
                cargo.setBalloonUUID(balloon.getUUID());
                balloon.setChildId(cargo.getUUID());
                level.setBlock(blockpos, blockstate.getFluidState().createLegacyBlock(), 3);
            }
            if (!level.isClientSide) {
                level.gameEvent(player, GameEvent.ENTITY_PLACE, blockpos);
                level.addFreshEntity(balloon);
            }

            itemstack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.CONSUME;
        }
    }
}
