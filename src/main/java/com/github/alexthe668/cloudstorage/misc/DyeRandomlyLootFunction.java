package com.github.alexthe668.cloudstorage.misc;

import com.github.alexthe668.cloudstorage.CommonProxy;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Random;

public class DyeRandomlyLootFunction extends LootItemConditionalFunction {

    DyeRandomlyLootFunction(LootItemCondition[] others) {
        super(others);
    }

    public LootItemFunctionType getType() {
        return CommonProxy.DYE_RANDOMLY_LOOT_FUNCTION;
    }

    public ItemStack run(ItemStack stack, LootContext context) {
        RandomSource random = context.getRandom();
        if(stack.getItem() instanceof DyeableLeatherItem leatherItem){
            leatherItem.setColor(stack, (int) (random.nextFloat() * 0xFFFFFF));
        }
        if(stack.getItem() == Items.FIREWORK_ROCKET){
            DyeColor dyecolor = Util.getRandom(DyeColor.values(), random);
            FireworkRocketItem.Shape shape = Util.getRandom(FireworkRocketItem.Shape.values(), random);
            int i = random.nextInt(3);
            return generateRandomFirework(stack, dyecolor, shape, i);
        }
        return stack;
    }

    private ItemStack generateRandomFirework(ItemStack firework, DyeColor color, FireworkRocketItem.Shape shape, int length) {
        ItemStack itemstack1 = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag compoundtag = itemstack1.getOrCreateTagElement("Explosion");
        List<Integer> list = Lists.newArrayList();
        list.add(color.getFireworkColor());
        compoundtag.putIntArray("Colors", list);
        compoundtag.putByte("Type", (byte)shape.getId());
        CompoundTag compoundtag1 = firework.getOrCreateTagElement("Fireworks");
        ListTag listtag = new ListTag();
        CompoundTag compoundtag2 = itemstack1.getTagElement("Explosion");
        if (compoundtag2 != null) {
            listtag.add(compoundtag2);
        }

        compoundtag1.putByte("Flight", (byte)length);
        if (!listtag.isEmpty()) {
            compoundtag1.put("Explosions", listtag);
        }

        return firework;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<DyeRandomlyLootFunction> {
        public void serialize(JsonObject p_80454_, DyeRandomlyLootFunction p_80455_, JsonSerializationContext p_80456_) {
            super.serialize(p_80454_, p_80455_, p_80456_);

        }

        public DyeRandomlyLootFunction deserialize(JsonObject p_80450_, JsonDeserializationContext p_80451_, LootItemCondition[] p_80452_) {
            return new DyeRandomlyLootFunction(p_80452_);
        }
    }
}