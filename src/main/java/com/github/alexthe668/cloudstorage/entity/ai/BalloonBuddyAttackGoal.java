package com.github.alexthe668.cloudstorage.entity.ai;

import com.github.alexthe668.cloudstorage.entity.*;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class BalloonBuddyAttackGoal extends Goal {

    private BalloonBuddyEntity badloon;
    private int punchCooldown = 0;
    private int punchTicks = 0;

    public BalloonBuddyAttackGoal(BalloonBuddyEntity badloon) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.badloon = badloon;
    }

    @Override
    public boolean canUse() {
        return badloon.getTarget() != null && badloon.getTarget().isAlive()  && badloon.fearOfBeingPoppedCooldown == 0 && !badloon.stopFlying() && !badloon.shouldFaceStopAttacking();
    }


    public void stop() {
        this.badloon.setFace(BalloonFace.NEUTRAL);
        this.badloon.setHandGesture(GloveGesture.IDLE);
        this.punchTicks = 0;
    }

    public void start() {
        this.punchCooldown = 0;
    }

    public void tick(){
        boolean moveTowardsTarget = true;
        float extraY = 1.3F;
        float distExtra = this.badloon.getPersonality() == BalloonFace.CRAZY ? 1.5F : 1F;
        Entity hand = this.badloon.getChild();
        if(hand == null){
            return;
        }
        if(badloon.getPersonality() == BalloonFace.SCARY || badloon.getPersonality() == BalloonFace.EYEPATCH){
            badloon.setAbilityTime(20);
        }
        if(this.punchCooldown > 0){
            this.punchCooldown--;
        }
        if(badloon.getTarget().fallDistance >= 3.0D){
            if(hand instanceof BadloonHandEntity glove){
                if(glove.getGesture() != GloveGesture.FLIPOFF && glove.getGesture() != GloveGesture.WAVE){
                    this.badloon.setHandGesture(badloon.getRandom().nextInt(2) == 0 ? GloveGesture.FLIPOFF : GloveGesture.WAVE);
                }
            }
        }else if(moveTowardsTarget){
            float speed = this.badloon.getPersonality() == BalloonFace.CRAZY ? 1.5F : 1F;
            double targetX = badloon.getTarget().getX();
            double targetZ = badloon.getTarget().getZ();
            if (badloon.verticalCollision && !badloon.isOnGround() && !badloon.hasLineOfSight(badloon.getTarget())) {
                Vec3 lookRotated = new Vec3(0F, 0F, 2F).yRot(-badloon.getYRot() * (float)(Math.PI / 180F));
                targetX = badloon.getX() + lookRotated.x;
                targetZ = badloon.getZ() + lookRotated.z;
            }
            this.badloon.getMoveControl().setWantedPosition(targetX, badloon.getTarget().getEyeY() + extraY, targetZ, speed);
        }
        if(this.punchCooldown == 0 && (hand.distanceTo(this.badloon.getTarget()) < this.badloon.getTarget().getBbWidth() + distExtra || hand.getBoundingBox().intersects(this.badloon.getTarget().getBoundingBox())) && badloon.doesDealDamage()){
            if(this.badloon.getPersonality() == BalloonFace.TROLL && !this.badloon.getTarget().isPassenger() && this.badloon.getTarget().isOnGround()){
                this.badloon.getTarget().startRiding(hand, true);
                this.badloon.setAbilityTime(100 + badloon.getRandom().nextInt(50));
            }else{
                punchTicks++;
                this.badloon.setHandGesture(GloveGesture.PUNCH);
                if(punchTicks > 3){
                    this.badloon.getTarget().hurt(this.badloon.getPersonality() == BalloonFace.EYEPATCH ? new DamageSource("sneak_balloon_attack") : DamageSource.mobAttack(this.badloon), 2);
                    this.badloon.setHandGesture(GloveGesture.IDLE);
                    punchTicks = 0;
                    this.punchCooldown = this.badloon.getPersonality() == BalloonFace.CRAZY ? 3 : 5 + badloon.getRandom().nextInt(10);
                }
            }

        }
    }

    public double getXZDistanceTo(Vec3 vec3){
        return Mth.sqrt((float) badloon.distanceToSqr(vec3.x, badloon.getY(), vec3.z));
    }


}
