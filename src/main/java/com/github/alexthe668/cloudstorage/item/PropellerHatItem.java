package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.misc.CSCreativeTab;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PropellerHatItem extends ArmorItem {

    public static final HatMaterial HAT_ARMOR_MATERIAL = new HatMaterial();

    public PropellerHatItem() {
        super(HAT_ARMOR_MATERIAL, EquipmentSlot.HEAD, new Item.Properties().tab(CSCreativeTab.INSTANCE));
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
        consumer.accept((net.minecraftforge.client.IItemRenderProperties) CloudStorage.PROXY.getISTERProperties(true));
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "cloudstorage:textures/entity/propeller_hat.png";
    }

    public int getDefaultTooltipHideFlags(@Nonnull ItemStack stack) {
        return ItemStack.TooltipPart.MODIFIERS.getMask();
    }

    private static class HatMaterial implements ArmorMaterial {

        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return 300;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 20;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(CSItemRegistry.BALLOON_BIT.get());
        }

        @Override
        public String getName() {
            return "propeller_hat";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    }
}
