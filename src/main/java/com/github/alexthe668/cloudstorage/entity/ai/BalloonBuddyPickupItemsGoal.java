package com.github.alexthe668.cloudstorage.entity.ai;

import com.github.alexthe668.cloudstorage.entity.BalloonBuddyEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonFace;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BalloonBuddyPickupItemsGoal extends Goal {

    private BalloonBuddyEntity balloonBuddy;

    public BalloonBuddyPickupItemsGoal(BalloonBuddyEntity balloonBuddy) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.balloonBuddy = balloonBuddy;
    }

    @Override
    public boolean canUse() {
        if(this.balloonBuddy.getPersonality() == BalloonFace.HAPPY){

        }
        return false;
    }


}
