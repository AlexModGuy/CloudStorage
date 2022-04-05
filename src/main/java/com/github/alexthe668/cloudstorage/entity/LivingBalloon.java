package com.github.alexthe668.cloudstorage.entity;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface LivingBalloon {

    int getBalloonColor();

    void setBalloonColor(int color);

    BalloonFace getFace();

    void setFace(BalloonFace face);

    Entity getChild();

    @Nullable
    UUID getChildId();

    void setChildId(@Nullable UUID uniqueId);

    float getPopProgress(float partialTick);

    default float getAbilityProgress(float partialTicks){
        return 0.0F;
    }

    default float getAlpha(float partialTicks){ return 1.0F; }
}
