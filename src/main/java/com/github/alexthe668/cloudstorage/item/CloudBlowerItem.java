package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.misc.CSCreativeTab;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class CloudBlowerItem extends Item {

    public CloudBlowerItem() {
        super(new Item.Properties().tab(CSCreativeTab.INSTANCE));
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept((net.minecraftforge.client.extensions.common.IClientItemExtensions) CloudStorage.PROXY.getISTERProperties(false));
    }

    public static int getUseTime(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null ? compoundtag.getInt("UseTime") : 0;
    }

    public static float getLerpedUseTime(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getTag();
        float prev = compoundtag != null ? (float)compoundtag.getInt("PrevUseTime") : 0F;
        float current = compoundtag != null ? (float)compoundtag.getInt("UseTime") : 0F;
        return prev + f * (current - prev);
    }

    public static void setUseTime(ItemStack stack, int useTime) {
        stack.getOrCreateTag().putInt("PrevUseTime", getUseTime(stack));
        stack.getOrCreateTag().putInt("UseTime", useTime);
    }

    public InteractionResultHolder<ItemStack> use(Level p_220123_, Player p_220124_, InteractionHand p_220125_) {
        ItemStack itemstack = p_220124_.getItemInHand(p_220125_);
        p_220124_.startUsingItem(p_220125_);
        return InteractionResultHolder.consume(itemstack);
    }

    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int timeUsing) {
        int i = getUseDuration(stack) - timeUsing;
        if(level.isClientSide){
            setUseTime(stack, i);
        }
        if(i > 5F){
            boolean left = isLeftHand(living);
            Vec3 particlesFrom = new Vec3(left ? 0.25F : -0.25F, living.getBbHeight() * 0.7F, 0.4F).xRot(-living.getXRot() * ((float)Math.PI / 180F)).yRot(-living.getYRot() * ((float)Math.PI / 180F)).add(living.position());
            Vec3 particlesTo = new Vec3(left ? 0.25F : -0.25F, living.getBbHeight() * 0.7F, 1F).xRot(-living.getXRot() * ((float)Math.PI / 180F)).yRot(-living.getYRot() * ((float)Math.PI / 180F)).add(living.position());

            Vec3 randomVec = new Vec3(living.getRandom().nextFloat() - 0.5F, living.getRandom().nextFloat() - 0.5F, living.getRandom().nextFloat() - 0.5F);
            particlesTo = particlesTo.add(randomVec.scale(0.3F));


            level.addParticle(CSParticleRegistry.BLOVIATOR_BREATH.get(), particlesFrom.x, particlesFrom.y, particlesFrom.z, particlesTo.x - particlesFrom.x, particlesTo.y - particlesFrom.y, particlesTo.z - particlesFrom.z);
        }
    }

    public boolean isLeftHand(LivingEntity entity){
        boolean leftHand = false;
        if (entity.getItemInHand(InteractionHand.MAIN_HAND).is(CSItemRegistry.CLOUD_BLOWER.get())) {
            leftHand = leftHand || entity.getMainArm() == HumanoidArm.LEFT;
        }
        if (entity.getItemInHand(InteractionHand.OFF_HAND).is(CSItemRegistry.CLOUD_BLOWER.get())) {
            leftHand = leftHand || entity.getMainArm() != HumanoidArm.LEFT;
        }
        return leftHand;
    }


    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int time) {
        setUseTime(stack, 0);
        stack.getOrCreateTag().putInt("PrevUseTime", 0);
    }

    public int getUseDuration(ItemStack p_220131_) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack p_220133_) {
        return UseAnim.NONE;
    }


}
