package com.github.alexthe668.cloudstorage.entity.ai;

import com.github.alexthe668.cloudstorage.entity.BadloonEntity;
import com.github.alexthe668.cloudstorage.entity.BloviatorEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BloviatorAttackGoal extends Goal {

    private BloviatorEntity cloud;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public BloviatorAttackGoal(BloviatorEntity cloud) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.cloud = cloud;
    }

    @Override
    public boolean canUse() {
        return cloud.getTarget() != null && cloud.getTarget().isAlive();
    }

    public void stop(){
        strafingTime = -1;
        cloud.getMoveControl().setWantedPosition(cloud.getX(), cloud.getY(), cloud.getZ(), 1.0F);
    }

    public void tick() {
        cloud.setNoActionTime(0);
        LivingEntity target = cloud.getTarget();
        if(target != null){
            double dist = cloud.distanceTo(target);
            double stayBackSize = 10 * cloud.getCloudScale();
            float cantSeeMod = cloud.hasLineOfSight(target) ? 0.0F : (float)Math.sin(cloud.tickCount * 0.05F) * 5F;
            if(cloud.isThundery()){
                double targetX = cloud.getTarget().getX();
                double targetZ = cloud.getTarget().getZ();
                if (cloud.verticalCollision && !cloud.isOnGround() && !cloud.hasLineOfSight(target)) {
                    Vec3 lookRotated = new Vec3(0F, 0F, 1F).yRot(-cloud.getYRot() * (float)(Math.PI / 180F));
                    targetX = cloud.getX() + lookRotated.x;
                    targetZ = cloud.getZ() + lookRotated.z;
                }
                this.cloud.getMoveControl().setWantedPosition(targetX, target.getY(1.0) + 1.5F * cloud.getCloudScale() + cantSeeMod, targetZ, 1.0F);
            }else{
                if(dist > stayBackSize || !cloud.canPush(target)){
                    double targetX = cloud.getTarget().getX();
                    double targetZ = cloud.getTarget().getZ();
                    if (cloud.verticalCollision && !cloud.isOnGround() && !cloud.hasLineOfSight(target)) {
                        Vec3 lookRotated = new Vec3(0F, 0F, 2F).yRot(-cloud.getYRot() * (float)(Math.PI / 180F));
                        targetX = cloud.getX() + lookRotated.x;
                        targetZ = cloud.getZ() + lookRotated.z;
                    }
                    this.cloud.getMoveControl().setWantedPosition(targetX, target.getY(1.0) + 1.0F + cantSeeMod, targetZ, 1.0F);
                }else{
                    this.strafingTime++;
                    if (this.strafingTime % 20 == 0) {
                        if ((double)cloud.getRandom().nextFloat() < 0.1D) {
                            this.strafingClockwise = !this.strafingClockwise;
                        }

                        if ((double)cloud.getRandom().nextFloat() < 0.1D) {
                            this.strafingBackwards = !this.strafingBackwards;
                        }
                    }
                    if (this.strafingTime > -1) {
                        if (dist > (double)(stayBackSize * 0.75F)) {
                            this.strafingBackwards = false;
                        } else if (dist < (double)(stayBackSize * 0.25F)) {
                            this.strafingBackwards = true;
                        }

                        cloud.getMoveControl().strafe(this.strafingBackwards ? -0.25F : 0.25F, this.strafingClockwise ? 0.1F : -0.1F);
                        cloud.lookAt(target, 30.0F, 30.0F);
                    } else {
                        cloud.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    }
                }

            }
        }
    }
}
