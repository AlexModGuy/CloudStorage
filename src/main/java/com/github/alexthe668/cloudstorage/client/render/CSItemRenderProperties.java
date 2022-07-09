package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe668.cloudstorage.client.ClientProxy;
import com.github.alexthe668.cloudstorage.client.model.PropellerHatModel;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;

public class CSItemRenderProperties implements IClientItemExtensions {
    private boolean armor;
    public static PropellerHatModel PROPELLER_HAT_MODEL;

    public CSItemRenderProperties(boolean armor){
        this.armor = armor;
    }

    @Nullable
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {   if(armor){
            if(itemStack.getItem() == CSItemRegistry.PROPELLER_HAT.get()){
                if(PROPELLER_HAT_MODEL == null){
                    PROPELLER_HAT_MODEL = new PropellerHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientProxy.PROPELLER_HAT_MODEL));
                }
                return PROPELLER_HAT_MODEL.withAnimations(livingEntity);
            }
        }
        return null;
    }

    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return this.armor ? Minecraft.getInstance().getItemRenderer().getBlockEntityRenderer() : new CSItemRenderer();
    }
}
