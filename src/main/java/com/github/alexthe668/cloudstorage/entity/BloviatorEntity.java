package com.github.alexthe668.cloudstorage.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class BloviatorEntity extends Monster {

    protected BloviatorEntity(EntityType type, Level level) {
        super(type, level);
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.15F).add(Attributes.FLYING_SPEED, 0.15F).add(Attributes.FOLLOW_RANGE, 32D);
    }
}
