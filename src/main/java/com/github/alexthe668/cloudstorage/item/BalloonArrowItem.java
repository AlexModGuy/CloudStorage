package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.github.alexthe668.cloudstorage.entity.CSEntityRegistry;
import com.github.alexthe668.cloudstorage.misc.CSCreativeTab;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BalloonArrowItem extends ArrowItem implements DyeableLeatherItem {


    public BalloonArrowItem(Properties props) {
        super(props);
    }

    public BalloonArrowItem() {
        this(new Properties().tab(CSCreativeTab.INSTANCE));
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
