package com.github.alexthe668.cloudstorage.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class FlyAroundGoal extends Goal {
    private final Mob flyer;
    private final int rangeXZ;
    private final int rangeY;
    private final int chance;
    private final float speed;
    private Vec3 moveToPoint = null;

    public FlyAroundGoal(Mob fly, int rangeXZ, int rangeY, int chance, float speed) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.flyer = fly;
        this.rangeXZ = rangeXZ;
        this.rangeY = rangeY;
        this.chance = chance;
        this.speed = speed;
    }

    public boolean canUse() {
        return flyer.getRandom().nextInt(chance) == 0 && !flyer.getMoveControl().hasWanted();
    }

    public void stop() {
        moveToPoint = null;
    }

    public boolean canContinueToUse() {
        return flyer.getMoveControl().hasWanted() && flyer.distanceToSqr(moveToPoint) > 1F;
    }

    public void start() {
        moveToPoint = this.getRandomLocation();
        if (moveToPoint != null) {
            flyer.getMoveControl().setWantedPosition(moveToPoint.x, moveToPoint.y, moveToPoint.z, speed);
        }
    }

    public void tick() {

    }

    @Nullable
    private Vec3 getRandomLocation() {
        Random random = flyer.getRandom();
        BlockPos blockpos = null;
        BlockPos origin = flyer.hasRestriction() ? this.flyer.getRestrictCenter() : flyer.blockPosition();
        for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = origin.offset(random.nextInt(rangeXZ * 2) - rangeXZ, random.nextInt(rangeY * 2) - rangeY, random.nextInt(rangeXZ * 2) - rangeXZ);
            if (canBlockPosBeSeen(blockpos1) && this.flyer.level.isEmptyBlock(blockpos1)) {
                blockpos = blockpos1;
            }
        }
        return blockpos == null ? null : new Vec3(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D);
    }

    public boolean canBlockPosBeSeen(BlockPos pos) {
        double x = pos.getX() + 0.5F;
        double y = pos.getY() + 0.5F;
        double z = pos.getZ() + 0.5F;
        HitResult result = flyer.level.clip(new ClipContext(new Vec3(flyer.getX(), flyer.getY() + (double) flyer.getEyeHeight(), flyer.getZ()), new Vec3(x, y, z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, flyer));
        double dist = result.getLocation().distanceToSqr(x, y, z);
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }

}
