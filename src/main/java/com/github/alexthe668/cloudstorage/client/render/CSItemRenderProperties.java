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
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CSItemRenderProperties implements IItemRenderProperties {
    private boolean armor;
    public static PropellerHatModel PROPELLER_HAT_MODEL;

    public CSItemRenderProperties(boolean armor){
        this.armor = armor;
    }

    @Nullable
    public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default){
        if(armor){
            if(itemStack.getItem() == CSItemRegistry.PROPELLER_HAT.get()){
                if(PROPELLER_HAT_MODEL == null){
                    PROPELLER_HAT_MODEL = new PropellerHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientProxy.PROPELLER_HAT_MODEL));
                }
                return PROPELLER_HAT_MODEL.withAnimations(entityLiving);
            }
        }
        return null;
    }

    public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return this.armor ? Minecraft.getInstance().getItemRenderer().getBlockEntityRenderer() : new CSItemRenderer();
    }
}
