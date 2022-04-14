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
import com.github.alexthe668.cloudstorage.misc.CSSoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BalloonItem extends Item implements DyeableLeatherItem {

    public static final int DEFAULT_COLOR = 0XE72929;
    private static final int[] BALLOONCOLORS = new int[]{DEFAULT_COLOR};

    public BalloonItem(Item.Properties props) {
        super(props);
    }

    public BalloonItem() {
        this(new Item.Properties().stacksTo(8).tab(CloudStorage.TAB));
    }

    public static int getBalloonColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : DEFAULT_COLOR;
    }

    public static boolean isStatic(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.getBoolean("static");
    }

    public static int get3DRender(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null ? compoundtag.getInt("3DRender") : 0;

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
            if(this == CSItemRegistry.BALLOON.get()){
                for (int i = 0; i < BALLOONCOLORS.length; i++) {
                    list.add(createBalloon(DEFAULT_COLOR, true));
                }
            }
        }
    }

    public ItemStack createBalloon(int color, boolean lightning) {
        ItemStack stack = new ItemStack(this);
        this.setColor(stack, color);
        this.setStatic(stack, lightning);
        return stack;
    }

    public int getColor(ItemStack stack) {
        return getBalloonColor(stack);
    }

    @Override
    public void setColor(ItemStack stack, int colorHex) {
        if (colorHex != DEFAULT_COLOR) {
            stack.getOrCreateTagElement("display").putInt("color", colorHex);
        }
    }

    public void setStatic(ItemStack stack, boolean isStatic) {
        if (isStatic) {
            stack.getOrCreateTag().putBoolean("static", true);
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        boolean rightHand = false;
        boolean leftHand = false;
        if (entity instanceof LivingEntity) {
            LivingEntity player = (LivingEntity) entity;
            boolean flag = held || player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof BalloonItem;
            if (flag) {
                if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof BalloonItem) {
                    leftHand = leftHand || player.getMainArm() == HumanoidArm.LEFT;
                    rightHand = rightHand || player.getMainArm() == HumanoidArm.RIGHT;
                }
                if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof BalloonItem) {
                    leftHand = leftHand || player.getMainArm() != HumanoidArm.LEFT;
                    rightHand = rightHand || player.getMainArm() != HumanoidArm.RIGHT;
                }
                if (leftHand) {
                    if (entity.getDeltaMovement().y < 0) {
                        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1F, 0.95F, 1F));
                    }
                }
                if (rightHand) {
                    if (entity.getDeltaMovement().y < 0) {
                        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1F, 0.95F, 1F));
                    }
                }
                if(rightHand || leftHand){
                    entity.fallDistance *= 0.9F;
                }
            }
        }
    }

    public boolean placeBalloon(Level level, ItemStack itemstack, BlockPos blockpos, Direction direction, @Nullable Player player, boolean dispensed){
        BlockState blockstate = level.getBlockState(blockpos);
        BlockPos blockpos1 = blockpos.relative(direction);
        if(dispensed){
            blockpos = blockpos1;
            blockstate = level.getBlockState(blockpos1);
        }
        Random random = new Random();
        if (!isStatic(itemstack) && blockstate.is(CSBlockRegistry.STATIC_CLOUD.get())) {
            CompoundTag tag = itemstack.getOrCreateTag().copy();
            tag.putBoolean("static", true);
            ItemStack copyOff = itemstack.copy();
            copyOff.setCount(1);
            copyOff.setTag(tag);
            level.playSound(player, blockpos1, CSSoundRegistry.STATIC_SHOCK, SoundSource.BLOCKS, 0.5F, 0.75F + random.nextFloat() * 0.5F);
            if(player != null && !dispensed){
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                if (!player.addItem(copyOff)) {
                    player.drop(copyOff, true);
                }
            }else{
                Block.popResource(level, blockpos, copyOff);
            }
            level.setBlockAndUpdate(blockpos, CSBlockRegistry.CLOUD.get().defaultBlockState());
            return true;
        } else if (isStatic(itemstack) && blockstate.is(CSBlockRegistry.CLOUD.get())) {
            CompoundTag tag = itemstack.getOrCreateTag().copy();
            tag.putBoolean("static", false);
            ItemStack copyOff = itemstack.copy();
            copyOff.setCount(1);
            copyOff.setTag(tag);
            if(player != null && !dispensed){
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                if (!player.addItem(copyOff)) {
                    player.drop(copyOff, true);
                }
            }else{
                Block.popResource(level, blockpos, copyOff);
            }
            level.setBlockAndUpdate(blockpos, CSBlockRegistry.STATIC_CLOUD.get().defaultBlockState());
            return true;
        } else if (player != null && ((!isStatic(itemstack) && blockstate.is(CSBlockRegistry.CLOUD_CHEST.get())) || (isStatic(itemstack) && blockstate.is(CSBlockRegistry.STATIC_CLOUD_CHEST.get())))) {
            BlockEntity te = level.getBlockEntity(blockpos);
            if (te instanceof AbstractCloudChestBlockEntity cloudChest) {
                if (cloudChest.hasBalloonFor(player)) {
                    ItemStack newBalloon = new ItemStack(CSItemRegistry.BALLOON.get());
                    newBalloon.setCount(1);
                    BalloonItem newBalloonItem = (BalloonItem) newBalloon.getItem();
                    newBalloonItem.setColor(newBalloon, cloudChest.getBalloonFor(player));
                    newBalloonItem.setStatic(newBalloon, cloudChest.getBalloonStaticFor(player));
                    ItemEntity itemEntity = new ItemEntity(level, blockpos.getX() + 0.5F, blockpos.getY() + 0.75F, blockpos.getZ() + 0.5F, newBalloon);
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
                cloudChest.setBalloonColorFor(player, this.getColor(itemstack));
                cloudChest.setBalloonStaticFor(player, isStatic(itemstack));
                itemstack.shrink(1);
                return true;
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
        if (level.noCollision(balloon) || dispensed) {
            if (blockstate.is(BlockTags.FENCES) || blockstate.getBlock() == CSBlockRegistry.BALLOON_STAND.get()) {
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
            return true;
        } else {
            return false;
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        return placeBalloon(level, itemstack, blockpos, direction, player, false) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.CONSUME;
    }
}
