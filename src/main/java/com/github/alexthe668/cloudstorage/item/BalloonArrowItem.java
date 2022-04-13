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
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
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

public class BalloonArrowItem extends ArrowItem implements DyeableLeatherItem {


    public BalloonArrowItem(Properties props) {
        super(props);
    }

    public BalloonArrowItem() {
        this(new Properties().tab(CloudStorage.TAB));
    }

    public static int getBalloonColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : BalloonItem.DEFAULT_COLOR;
    }

    public boolean hasCustomColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) && compoundtag.getInt("color") != BalloonItem.DEFAULT_COLOR;
    }

    public int getColor(ItemStack stack) {
        return getBalloonColor(stack);
    }

    @Override
    public void setColor(ItemStack stack, int colorHex) {
        if (colorHex != BalloonItem.DEFAULT_COLOR) {
            stack.getOrCreateTagElement("display").putInt("color", colorHex);
        }
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity entity) {
        Arrow arrow = new Arrow(level, entity);
        BalloonEntity balloon = CSEntityRegistry.BALLOON.get().create(level);
        balloon.setPos(entity.getX(), entity.getY(1.0), entity.getZ());
        balloon.setChildId(arrow.getUUID());
        balloon.setBalloonColor(getBalloonColor(stack));
        balloon.setStringLength(0);
        balloon.setArrow(true);
        level.addFreshEntity(balloon);
        arrow.setEffectsFromItem(stack);
        return arrow;
    }
}
