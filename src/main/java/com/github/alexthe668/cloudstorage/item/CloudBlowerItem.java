package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.misc.CSCreativeTab;
import com.github.alexthe668.cloudstorage.misc.CSSoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class CloudBlowerItem extends Item {

    public static int MAX_FUEL = 300;

    public CloudBlowerItem() {
        super(new Item.Properties().stacksTo(1));
    }

    public static int getMode(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null ? compoundtag.getInt("Mode") : 0;
    }

    public static void setMode(ItemStack stack, int mode) {
        stack.getOrCreateTag().putInt("Mode", mode);
    }

    public static int getUseTime(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null ? compoundtag.getInt("UseTime") : 0;
    }

    public static float getLerpedUseTime(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getTag();
        float prev = compoundtag != null ? (float) compoundtag.getInt("PrevUseTime") : 0F;
        float current = compoundtag != null ? (float) compoundtag.getInt("UseTime") : 0F;
        return prev + f * (current - prev);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        if(!stack.hasTag()){
            CompoundTag compoundtag = stack.getOrCreateTag();
            stack.setTag(compoundtag);
        }
        if(getUseTime(stack) != 0 && entity instanceof LivingEntity living && !living.getUseItem().equals(stack)){
            setUseTime(stack, 0);
            stack.getOrCreateTag().putInt("PrevUseTime", 0);

        }
    }

    public static void setUseTime(ItemStack stack, int useTime) {
        stack.getOrCreateTag().putInt("PrevUseTime", getUseTime(stack));
        stack.getOrCreateTag().putInt("UseTime", useTime);
    }

    public static int getFuel(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.contains("Fuel") ? compoundtag.getInt("Fuel") : MAX_FUEL;
    }

    public static void setFuel(ItemStack stack, int mode) {
        stack.getOrCreateTag().putInt("Fuel", mode);
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flags) {
        super.appendHoverText(stack, level, components, flags);
        if(stack.hasTag()){
            MutableComponent one = Component.translatable("item.cloudstorage.cloud_blower.mode").withStyle(ChatFormatting.GRAY);
            MutableComponent two = Component.translatable("item.cloudstorage.cloud_blower.mode_" + getMode(stack)).withStyle(ChatFormatting.AQUA);
            components.add(one.append(two));
        }
        components.add(Component.translatable("item.cloudstorage.cloud_blower.desc_0").withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("item.cloudstorage.cloud_blower.desc_1").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept((net.minecraftforge.client.extensions.common.IClientItemExtensions) CloudStorage.PROXY.getISTERProperties(false));
    }

    public InteractionResultHolder<ItemStack> use(Level lvl, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(getFuel(itemstack) <= 0){
            ItemStack ammo = findAmmo(player);
            boolean flag = player.isCreative();
            if(!ammo.isEmpty() && !flag){
                player.addItem(ammo.getCraftingRemainingItem().copy());
                ammo.shrink(1);
                flag = true;
            }
            if(flag){
                setFuel(itemstack, MAX_FUEL);
            }
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int timeUsing) {
        if(getFuel(stack) > 0){
            int i = getUseDuration(stack) - timeUsing;
            if (level.isClientSide) {
                setUseTime(stack, i);
            }
            if (i >= 5) {
                if((i + 10) % 15 == 0){
                    living.playSound(CSSoundRegistry.CLOUD_BLOWER.get());
                    living.gameEvent(GameEvent.ITEM_INTERACT_START);
                }
                boolean left = isLeftHand(living);
                float totalDistance = 6.5F;
                Vec3 handAt = living.getEyePosition().add(0, -0.25F, 0);
                Vec3 windFrom = new Vec3(left ? 0.25F : -0.25F, 0, 0.35F).xRot(-living.getXRot() * ((float) Math.PI / 180F)).yRot(-living.getYRot() * ((float) Math.PI / 180F)).add(handAt);
                Vec3 windTo = new Vec3(left ? 0.25F : -0.25F, 0, totalDistance).xRot(-living.getXRot() * ((float) Math.PI / 180F)).yRot(-living.getYRot() * ((float) Math.PI / 180F)).add(handAt);
                BlockHitResult result = level.clip(new ClipContext(windFrom, windTo, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, living));
                if (result != null) {
                    windTo = result.getLocation();
                }
                Vec3 sub = windTo.subtract(handAt);
                int dist = (int)Math.ceil(sub.length()) + 1;
                for(int j = 0; j < dist; j++){
                    float scale = (j + 1) / (float)dist;
                    Vec3 moveMobsFrom = handAt.add(sub.scale(scale));
                    float aabbFan = 1F + scale * 0.5F;
                    //level.addParticle(ParticleTypes.FLAME, moveMobsFrom.x, moveMobsFrom.y, moveMobsFrom.z, 0, 0 ,0);
                    for(Entity entity : level.getEntities(living, new AABB(moveMobsFrom.subtract(aabbFan, aabbFan, aabbFan), moveMobsFrom.add(aabbFan, aabbFan + 1, aabbFan)))){
                        if(living.hasLineOfSight(entity)){
                            pullOrPush(entity, stack, windFrom);
                        }
                    }
                }
                if(getMode(stack) == 2){
                    Vec3 away = living.position().subtract(windTo).normalize().scale(0.1F);
                    living.setDeltaMovement(living.getDeltaMovement().add(away));
                    living.fallDistance = 0.0F;
                }
                Vec3 randomVec = new Vec3(living.getRandom().nextFloat() - 0.5F, living.getRandom().nextFloat() - 0.5F, living.getRandom().nextFloat() - 0.5F);
                Vec3 particleDir = windTo.add(randomVec).subtract(windFrom).normalize();
                if(getMode(stack) == 1){
                    Vec3 priorWindFrom = windFrom;
                    windFrom = windTo.add(randomVec);
                    particleDir = priorWindFrom.subtract(windFrom).normalize().scale(0.6F);
                }
                level.addParticle(CSParticleRegistry.BLOVIATOR_BREATH.get(), windFrom.x, windFrom.y, windFrom.z, particleDir.x, particleDir.y, particleDir.z);
                if(i % 15 == 0 && (!(living instanceof Player) || !((Player) living).isCreative())){
                    setFuel(stack, getFuel(stack) - 1);
                }
            }
        }else{
            living.stopUsingItem();
        }
    }

    private void pullOrPush(Entity entity, ItemStack stack, Vec3 windFrom) {
        if(getMode(stack) == 1){
            Vec3 towards = windFrom.subtract(entity.position());
            float pull = towards.length() > 2F ? 1F : (float) (2F - towards.length()) * 0.5F;
            entity.setDeltaMovement(entity.getDeltaMovement().add(towards.normalize()).scale(pull * 0.45F));
        }else{
            Vec3 away = entity.position().subtract(windFrom);
            float scale = 0.2F;
            if(getMode(stack) == 2){
                scale = 0.05F;
            }
            entity.setDeltaMovement(entity.getDeltaMovement().add(away.normalize().scale(scale)));
        }
        entity.setOnGround(false);
        entity.fallDistance = 0.0f;
    }

    public boolean isLeftHand(LivingEntity entity) {
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

    public boolean isBarVisible(ItemStack stack) {
        return getFuel(stack) != MAX_FUEL;
    }

    public int getBarWidth(ItemStack stack) {
        float f = (float)Math.min(getFuel(stack), MAX_FUEL) / (float)MAX_FUEL;
        return Math.round(f * 13);
    }

    public int getBarColor(ItemStack p_150901_) {
        return 0X24AFFF;
    }

    public ItemStack findAmmo(Player entity) {
        if(entity.isCreative()){
            return ItemStack.EMPTY;
        }
        for(int i = 0; i < entity.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack1 = entity.getInventory().getItem(i);
            if (itemstack1.is(CSItemRegistry.ANGRY_CLOUD_IN_A_BOTTLE.get()) || itemstack1.is(CSItemRegistry.HAPPY_CLOUD_IN_A_BOTTLE.get())) {
                return itemstack1;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void onLeftClick(Player playerIn, ItemStack stack) {
        if(stack.getItem() == CSItemRegistry.CLOUD_BLOWER.get()) {
            int current = getMode(stack);
            int next = current + 1 > 2 ? 0 : current + 1;
            setMode(stack, next);
            String modeStr = "item.cloudstorage.cloud_blower.mode_" + next;
            playerIn.playSound(SoundEvents.COMPARATOR_CLICK, 0.5F, 1.4F);
            playerIn.displayClientMessage(Component.translatable("item.cloudstorage.cloud_blower.change_mode").append(Component.translatable(modeStr).withStyle(ChatFormatting.AQUA)), true);
        }
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(CSItemRegistry.CLOUD_BLOWER.get()) || !newStack.is(CSItemRegistry.CLOUD_BLOWER.get());
    }
}
