package com.github.alexthe668.cloudstorage.entity.ai;

import com.github.alexthe668.cloudstorage.entity.BadloonEntity;
import com.github.alexthe668.cloudstorage.entity.BadloonHandEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonFace;
import com.github.alexthe668.cloudstorage.entity.GloveGesture;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class BadloonAttackGoal extends Goal {

    private BadloonEntity badloon;
    private Entity pickupMonster = null;
    private int tryPickupCheckIn = 0;
    private int punchCooldown = 0;
    private int punchTicks = 0;
    public BadloonAttackGoal(BadloonEntity badloon) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.badloon = badloon;
    }

    @Override
    public boolean canUse() {
        return badloon.getTarget() != null && badloon.getTarget().isAlive()  && badloon.fearOfBeingPoppedCooldown == 0;
    }


    public void stop() {
        this.badloon.setFace(BalloonFace.NEUTRAL);
        this.badloon.setHandGesture(GloveGesture.IDLE);
        this.pickupMonster = null;
        this.punchTicks = 0;
    }

    public void start() {
        this.badloon.setFace(BalloonFace.ANGRY);
        this.tryPickupCheckIn = 0;
        this.punchCooldown = 0;
        if(this.badloon.getChild() instanceof BadloonHandEntity hand){
            if(!hand.getPassengers().isEmpty()){
                this.pickupMonster = hand.getPassengers().get(0);
            }
        }
    }

    public void tick(){
        badloon.setNoActionTime(0);
        boolean moveTowardsTarget = true;
        float extraY = 1.3F;
        Entity hand = this.badloon.getChild();
        if(hand == null){
            return;
        }
        if(this.punchCooldown > 0){
            this.punchCooldown--;
        }
        if(pickupMonster != null){
            if(pickupMonster.isPassengerOfSameVehicle(hand)){
                this.badloon.setHandGesture(GloveGesture.GRAB);
                if(hand.getY() > badloon.getTarget().getY() + pickupMonster.getBbHeight() && getXZDistanceTo(badloon.getTarget().position()) < 0.5F){
                    dropMob();
                }
            }else{
                if(pickupMonster.isPassenger()){
                    pickupMonster = null;
                }else{
                    this.badloon.getMoveControl().setWantedPosition(pickupMonster.getX(), pickupMonster.getEyeY() + 1.2F, pickupMonster.getZ(), 1.0F);
                    if(hand.distanceTo(pickupMonster) < 3){
                        pickupMonster.startRiding(hand, true);
                    }
                    moveTowardsTarget = false;
                }
            }
        } else{
            if(tryPickupCheckIn > 0){
                tryPickupCheckIn--;
            }else{
                findMobToPickup();
                tryPickupCheckIn = 50;
            }
        }
        if(badloon.getTarget().fallDistance >= 3.0D){
            if(this.pickupMonster != null && this.pickupMonster.isPassengerOfSameVehicle(hand)){
                dropMob();
            }
            if(hand instanceof BadloonHandEntity glove){
                if(glove.getGesture() != GloveGesture.FLIPOFF && glove.getGesture() != GloveGesture.WAVE){
                    this.badloon.setHandGesture(badloon.getRandom().nextInt(2) == 0 ? GloveGesture.FLIPOFF : GloveGesture.WAVE);
                }
            }
        }else if(moveTowardsTarget){
            double targetX = badloon.getTarget().getX();
            double targetZ = badloon.getTarget().getZ();

            if (badloon.verticalCollision && !badloon.isOnGround() && !badloon.hasLineOfSight(badloon.getTarget())) {
                Vec3 lookRotated = new Vec3(0F, 0F, 2F).yRot(-badloon.getYRot() * (float)(Math.PI / 180F));
                targetX = badloon.getX() + lookRotated.x;
                targetZ = badloon.getZ() + lookRotated.z;
            }
            this.badloon.getMoveControl().setWantedPosition(targetX, badloon.getTarget().getEyeY() + extraY, targetZ, 1.0F);
        }
        if(pickupMonster == null && this.punchCooldown == 0 && (hand.distanceTo(this.badloon.getTarget()) < this.badloon.getTarget().getBbWidth() + 0.5F || hand.getBoundingBox().intersects(this.badloon.getTarget().getBoundingBox()))){
            punchTicks++;
            this.badloon.setHandGesture(GloveGesture.PUNCH);
            if(punchTicks > 3){
                this.badloon.getTarget().hurt(DamageSource.mobAttack(this.badloon), 2);
                this.badloon.setHandGesture(GloveGesture.IDLE);
                punchTicks = 0;
                this.punchCooldown = 5 + badloon.getRandom().nextInt(10);
            }

        }
    }


    public void findMobToPickup(){
        Predicate<Entity> monsterAway = (animal) -> animal instanceof Monster && !(animal instanceof BadloonEntity) && animal.distanceTo(badloon.getTarget()) > 5 && !animal.isPassenger();
        List<Mob> list = badloon.level.getEntitiesOfClass(Mob.class, badloon.getTarget().getBoundingBox().inflate(30, 12, 30), EntitySelector.NO_SPECTATORS.and(monsterAway));
        list.sort(Comparator.comparingDouble(badloon::distanceToSqr));
        if (!list.isEmpty()) {
            pickupMonster = list.get(0);
        }
    }
    public void dropMob(){
        pickupMonster.stopRiding();
        if(pickupMonster instanceof Creeper){
            ((Creeper)pickupMonster).ignite();
        }
        this.punchCooldown = 30;
        pickupMonster = null;
    }

    public double getXZDistanceTo(Vec3 vec3){
        return Mth.sqrt((float) badloon.distanceToSqr(vec3.x, badloon.getY(), vec3.z));
    }


}
