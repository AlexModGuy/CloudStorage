package com.github.alexthe668.cloudstorage.entity.ai;

import com.github.alexthe668.cloudstorage.entity.BadloonEntity;
import com.github.alexthe668.cloudstorage.entity.BloviatorEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

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
        LivingEntity target = cloud.getTarget();
        if(target != null){
            double dist = cloud.distanceTo(target);
            double stayBackSize = 10 * cloud.getCloudScale();
            float cantSeeMod = cloud.hasLineOfSight(target) ? 0.0F : (float)Math.sin(cloud.tickCount * 0.05F) * 5F;
            if(cloud.isThundery()){
                cloud.getMoveControl().setWantedPosition(target.getX(), target.getY(1.0) + 1.5F * cloud.getCloudScale() + cantSeeMod, target.getZ(), 1);
            }else{
                if(dist > stayBackSize || !cloud.canPush(target)){
                    cloud.getMoveControl().setWantedPosition(target.getX(), target.getY(1.0) + 1.0F + cantSeeMod, target.getZ(), 1);
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
