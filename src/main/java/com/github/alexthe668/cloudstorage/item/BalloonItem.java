package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.block.AbstractCloudChestBlockEntity;
import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.entity.BalloonCargoEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonTieEntity;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.misc.CSSoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BalloonItem extends Item implements DyeableLeatherItem, CustomTabBehavior {

    public static final int DEFAULT_COLOR = 0XE72929;
    private static final int[] BALLOONCOLORS = new int[]{DEFAULT_COLOR};
    public static final ResourceLocation LOOT_TABLE = new ResourceLocation(CloudStorage.MODID, "chests/loot_balloon");

    public BalloonItem(Item.Properties props) {
        super(props);
    }

    public BalloonItem() {
        this(new Item.Properties().stacksTo(8));
    }

    public static int getBalloonColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : DEFAULT_COLOR;
    }

    public static boolean isStatic(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.getBoolean("static") && !isLoot(stack);
    }

    public static boolean isLoot(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.getBoolean("LootBalloon");
    }

    public static int get3DRender(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null ? compoundtag.getInt("3DRender") : 0;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flags) {
        super.appendHoverText(stack, level, components, flags);
        if (!isLoot(stack) && isStatic(stack)) {
            components.add(Component.translatable("item.cloudstorage.balloon.static").withStyle(ChatFormatting.AQUA));
        }
    }

    public boolean hasCustomColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) && compoundtag.getInt("color") != DEFAULT_COLOR && compoundtag.getInt("color") != -1;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept((net.minecraftforge.client.extensions.common.IClientItemExtensions) CloudStorage.PROXY.getISTERProperties(false));
    }

    public void fillItemCategory(CreativeModeTab.Output contents) {
        for (int i = 0; i < BALLOONCOLORS.length; i++) {
            contents.accept(createBalloon(DEFAULT_COLOR, 0));
        }
        if(this == CSItemRegistry.BALLOON.get()){
            for (int i = 0; i < BALLOONCOLORS.length; i++) {
                contents.accept(createBalloon(DEFAULT_COLOR, 1));
            }
            contents.accept(createBalloon(-1, 2));
        }
    }

    public ItemStack createBalloon(int color, int type) {
        ItemStack stack = new ItemStack(this);
        this.setColor(stack, color);
        setStatic(stack, type == 1);
        setLoot(stack, type == 2);
        if(type == 2){
            stack.hideTooltipPart(ItemStack.TooltipPart.DYE);
        }
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

    public static void setStatic(ItemStack stack, boolean isStatic) {
        if (isStatic) {
            stack.getOrCreateTag().putBoolean("static", true);
        }
    }

    public static void setLoot(ItemStack stack, boolean isLoot) {
        if (isLoot) {
            stack.getOrCreateTag().putBoolean("LootBalloon", true);
        }
    }

    public Component getName(ItemStack stack) {
        return isLoot(stack) ? Component.translatable("item.cloudstorage.loot_balloon") : super.getName(stack);
    }

    public Rarity getRarity(ItemStack stack) {
        return isLoot(stack) ? Rarity.UNCOMMON : super.getRarity(stack);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        boolean rightHand = false;
        boolean leftHand = false;
        if(isLoot(stack) && getBalloonColor(stack) == -1){
            setColor(stack, level.getRandom().nextInt(0xFFFFFF));
        }
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
                updateBalloon(player, player.getItemInHand(InteractionHand.MAIN_HAND), player.getMainArm() == HumanoidArm.LEFT);
                updateBalloon(player, player.getItemInHand(InteractionHand.OFF_HAND), player.getMainArm() == HumanoidArm.RIGHT);
                if(rightHand || leftHand){
                    entity.fallDistance *= 0.9F;
                }
            }
        }
    }

    private void updateBalloon(LivingEntity player, ItemStack itemInHand, boolean left) {
        if(itemInHand.is(CSItemRegistry.BALLOON.get())){
            CloudStorage.PROXY.onHoldingBalloon(player, itemInHand, left);
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
        if (!isLoot(itemstack) && !isStatic(itemstack) && blockstate.is(CSBlockRegistry.STATIC_CLOUD.get())) {
            CompoundTag tag = itemstack.getOrCreateTag().copy();
            tag.putBoolean("static", true);
            ItemStack copyOff = itemstack.copy();
            copyOff.setCount(1);
            copyOff.setTag(tag);
            level.playSound(player, blockpos1, CSSoundRegistry.STATIC_SHOCK.get(), SoundSource.BLOCKS, 0.5F, 0.75F + random.nextFloat() * 0.5F);
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
                    newBalloonItem.setStatic(newBalloon, blockstate.is(CSBlockRegistry.STATIC_CLOUD_CHEST.get()));
                    newBalloonItem.setLoot(newBalloon, cloudChest.hasLootBalloon());
                    ItemEntity itemEntity = new ItemEntity(level, blockpos.getX() + 0.5F, blockpos.getY() + 0.75F, blockpos.getZ() + 0.5F, newBalloon);
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
                if(isLoot(itemstack)){
                    cloudChest.setLootBalloon(this.getColor(itemstack), LOOT_TABLE, random.nextLong());
                }else{
                    cloudChest.setLootBalloon(1, null, random.nextLong());
                    cloudChest.setBalloonColorFor(player, this.getColor(itemstack));
                }
                itemstack.shrink(1);
                return true;
            }
        }
        if(isLoot(itemstack)){
            return false;
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
            }
            if(level.addFreshEntity(balloon)){
                itemstack.shrink(1);
            }
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
