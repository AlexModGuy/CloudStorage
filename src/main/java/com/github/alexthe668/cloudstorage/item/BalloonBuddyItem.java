package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.entity.BalloonBuddyEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonFace;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;

public class BalloonBuddyItem extends BalloonItem {

    public BalloonBuddyItem() {
        super(new Item.Properties().stacksTo(1).tab(CloudStorage.TAB));
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = context.getLevel();
        BalloonBuddyEntity balloon = CSEntityRegistry.BALLOON_BUDDY.get().create(level);
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if(itemstack.getTag() != null && itemstack.getTag().contains("MobNBT") && itemstack.getTag().getCompound("MobNBT") != null){
            balloon.readAdditionalSaveData(itemstack.getTag().getCompound("MobNBT"));
        }else{
            balloon.setCommand(2);
        }
        balloon.setBalloonColor(this.getColor(itemstack));
        if(itemstack.getTag() != null && itemstack.getTag().contains("Health")){
            balloon.setHealth(itemstack.getTag().getFloat("Health"));
        }
        if (getPersonality(itemstack) != null) {
            balloon.setPersonality(getPersonality(itemstack));
        }
        if(player != null){
            balloon.setTame(true);
            balloon.setOwnerUUID(player.getUUID());
        }
        if(itemstack.hasCustomHoverName()){
            balloon.setCustomName(itemstack.getHoverName());
        }
        balloon.setPos(blockpos1.getX() + 0.5F, blockpos1.getY() + 0.5F, blockpos1.getZ() + 0.5F);
        if (level.noCollision(balloon)) {
            if (!level.isClientSide) {
                level.gameEvent(player, GameEvent.ENTITY_PLACE, blockpos);
                level.addFreshEntity(balloon);
            }
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flags) {
        if (getPersonality(stack) != null) {
            components.add(new TranslatableComponent(getPersonality(stack).getPersonalityText()).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, components, flags);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowdedIn(tab)) {
            for (int i = BalloonFace.HAPPY.ordinal(); i < BalloonFace.values().length; i++) {
                list.add(createBalloon(DEFAULT_COLOR, BalloonFace.values()[i]));
            }
        }
    }

    public ItemStack createBalloon(int color, BalloonFace face) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", color);
        tag.putInt("Personality", face.ordinal());
        ItemStack stack = new ItemStack(this);
        stack.setTag(tag);
        return stack;
    }

    public static BalloonFace getPersonality(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.contains("Personality", 99) && compoundtag.getInt("Personality") != -1 ? BalloonFace.values()[Mth.clamp(compoundtag.getInt("Personality"), 0,  BalloonFace.values().length - 1)]  : null;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        if(!level.isClientSide && this.getPersonality(stack) == null){
            CompoundTag compoundtag = stack.getOrCreateTag();
            compoundtag.putInt("Personality", BalloonFace.getRandomPersonality(level.getRandom()).ordinal());
            stack.setTag(compoundtag);
        }
    }
}
