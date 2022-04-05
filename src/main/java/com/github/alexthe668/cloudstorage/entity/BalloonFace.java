package com.github.alexthe668.cloudstorage.entity;

import net.minecraft.util.Mth;

import java.util.Locale;
import java.util.Random;

public enum BalloonFace {
    NEUTRAL(false, true),
    ANGRY(false, true),
    SCARED(false, false),
    HAPPY(true, false),
    SMIRK(true, true),
    SCARY(true, true),
    EYEPATCH(true, true),
    CRAZY(true, true),
    TROLL(true, true),
    CHARMING(true, false),
    COOL(true, false);

    private boolean personality;
    private boolean melees;

    BalloonFace(boolean personality, boolean melees){
        this.personality = personality;
        this.melees = melees;
    }

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

    public boolean isPersonality() {
        return personality;
    }

    public boolean doesMeleeAttacks() {
        return melees;
    }

    public static BalloonFace getRandomPersonality(Random random){
        int index = random.nextInt(values().length - 3) + 3;
        return BalloonFace.values()[Mth.clamp(index, 0, values().length)];
    }

    public String getPersonalityText(){
        return "entity.cloudstorage.balloon_buddy.personality." + this.name().toLowerCase(Locale.ROOT);
    }
}
