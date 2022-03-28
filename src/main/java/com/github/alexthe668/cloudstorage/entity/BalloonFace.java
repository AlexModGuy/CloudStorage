package com.github.alexthe668.cloudstorage.entity;

import net.minecraft.util.Mth;

import java.util.Locale;

public enum BalloonFace {
    NEUTRAL, ANGRY, SCARED;

    public String getName(){
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static float rotlerp(float original, float target, float p_24994_) {
        float f = Mth.wrapDegrees(target - original);
        if (f > p_24994_) {
            f = p_24994_;
        }

        if (f < -p_24994_) {
            f = -p_24994_;
        }

        float f1 = original + f;
        if (f1 < 0.0F) {
            f1 += 360.0F;
        } else if (f1 > 360.0F) {
            f1 -= 360.0F;
        }

        return f1;
    }
}
