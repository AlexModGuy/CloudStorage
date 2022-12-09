package com.github.alexthe668.cloudstorage.item;

import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.misc.CSCreativeTab;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class CloudBottleItem extends Item {

    private boolean happy;

    public CloudBottleItem(boolean happy) {
        super(new Item.Properties().stacksTo(8).craftRemainder(happy ? Items.GLASS_BOTTLE : null));
        this.happy = happy;
    }
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, happy ? 25 : 40);
        float up = (happy ? 1.5F : 1.0F) + player.getRandom().nextFloat() * 0.3F;
        player.setDeltaMovement(player.getDeltaMovement().add(0, up, 0));
        player.fallDistance /= 2.0D;
        for(int i = 0; i < 12 + player.getRandom().nextInt(12); i++){
            level.addParticle(CSParticleRegistry.BLOVIATOR_BREATH.get(), player.getRandomX(0.5F), player.getY(0.5F), player.getRandomZ(0.5F), 0, up * (0.1F + player.getRandom().nextFloat() * 0.2F), 0);
        }
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
        if(!player.addItem(bottle)){
            player.spawnAtLocation(bottle);
        }
        return InteractionResultHolder.consume(itemstack);
    }
}
