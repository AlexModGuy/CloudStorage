package com.github.alexthe668.cloudstorage.entity.ai;

import com.github.alexthe668.cloudstorage.entity.BadloonEntity;
import com.github.alexthe668.cloudstorage.entity.BalloonFace;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BadloonFearCactusGoal extends Goal {
    private final int searchLength;
    private final int verticalSearchRange;
    protected BlockPos destinationBlock;
    protected int runDelay = 30;
    private Vec3 flightTarget;
    private BadloonEntity badloon;
    private float speed;

    public BadloonFearCactusGoal(BadloonEntity badloon, float speed) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        searchLength = 10;
        verticalSearchRange = 5;
        this.badloon = badloon;
        this.speed = speed;
    }

    public boolean canContinueToUse() {
        return destinationBlock != null && isCactus(badloon.level, destinationBlock.mutable()) && isCloseToCactus(10);
    }

    public boolean isCloseToCactus(double dist) {
        return destinationBlock == null || badloon.distanceToSqr(Vec3.atCenterOf(destinationBlock)) < dist * dist;
    }

    @Override
    public boolean canUse() {
        if (this.runDelay > 0) {
            --this.runDelay;
            return false;
        } else {
            this.runDelay = 40 + badloon.getRandom().nextInt(70);
            return this.searchForDestination();
        }
    }

    public Vec3 getPosAwayFrom(Vec3 fleePos, float radiusAdd) {
        float radius = -radiusAdd - badloon.getRandom().nextInt(5);
        float neg = badloon.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = badloon.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (badloon.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos pos = new BlockPos(fleePos.x() + extraX, badloon.getY(), fleePos.z() + extraZ);
        return Vec3.atCenterOf(pos);
    }


    public void start() {
        Vec3 vec = getPosAwayFrom(Vec3.atCenterOf(destinationBlock), 10);
        badloon.setFace(BalloonFace.SCARED);
        if (vec != null) {
            flightTarget = vec;
            badloon.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, speed);
        }
    }

    public void tick() {
        if (this.isCloseToCactus(10)) {
            badloon.setFace(BalloonFace.SCARED);
            if (flightTarget == null || badloon.distanceToSqr(flightTarget) < 2F) {
                Vec3 vec = getPosAwayFrom(Vec3.atCenterOf(destinationBlock), 10);
                if (vec != null) {
                    flightTarget = vec;
                }
            }
            if (flightTarget != null) {
                badloon.getMoveControl().setWantedPosition(flightTarget.x, flightTarget.y, flightTarget.z, speed);
            }
        }
    }

    public void stop() {
        flightTarget = null;
        badloon.setFace(BalloonFace.NEUTRAL);
        badloon.fearOfBeingPoppedCooldown = 100 + badloon.getRandom().nextInt(100);
    }

    protected boolean searchForDestination() {
        int i = this.searchLength;
        int j = this.verticalSearchRange;
        BlockPos blockpos = this.badloon.blockPosition();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int k = -5; k <= j; k++) {
            for(int l = 0; l < i; ++l) {
                for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, i1, k, j1);
                        if (this.badloon.isWithinRestriction(blockpos$mutableblockpos) && this.isCactus(this.badloon.level, blockpos$mutableblockpos)) {
                            this.destinationBlock = blockpos$mutableblockpos;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isCactus(Level world, BlockPos.MutableBlockPos pos) {
        return world.getBlockState(pos).is(Blocks.CACTUS);
    }

}
