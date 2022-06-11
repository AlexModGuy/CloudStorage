package com.github.alexthe668.cloudstorage.entity.ai;

import com.github.alexthe668.cloudstorage.entity.BalloonBuddyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;

import java.util.EnumSet;

public class BalloonBuddyFollowGoal extends Goal {
    private final BalloonBuddyEntity balloon;
    private LivingEntity owner;
    private final LevelReader world;
    private final double followSpeed;
    private int timeToRecalcPath;
    private final float maxDist;
    private final float minDist;
    private final boolean teleportToLeaves;

    public BalloonBuddyFollowGoal(BalloonBuddyEntity tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
        this.balloon = tameable;
        this.world = tameable.level;
        this.followSpeed = speed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.teleportToLeaves = teleportToLeaves;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity livingentity = this.balloon.getOwner();
        if (livingentity == null) {
            return false;
        } else if (livingentity.isSpectator()) {
            return false;
        } else if (this.balloon.isOrderedToSit()) {
            return false;
        } else if (this.balloon.distanceToSqr(livingentity) < (double)(this.minDist * this.minDist)) {
            return false;
        } else {
            this.owner = livingentity;
            return this.balloon.shouldFollow();
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        if (this.balloon.isOrderedToSit()) {
            return false;
        } else {
            return this.balloon.distanceToSqr(this.owner) > (double)(this.maxDist * this.maxDist);
        }
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }

    public void stop() {
        this.owner = null;
        balloon.clearMovement();
    }

    public void tick() {
        this.balloon.getLookControl().setLookAt(this.owner, 10.0F, (float)this.balloon.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.balloon.isLeashed() && !this.balloon.isPassenger()) {
                if (this.balloon.distanceToSqr(this.owner) >= 144.0D) {
                    this.tryToTeleportNearEntity();
                }
                balloon.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + owner.getBbHeight(), owner.getZ(), followSpeed);
            }
        }
    }

    private void tryToTeleportNearEntity() {
        BlockPos blockpos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomNumber(-3, 3);
            int k = this.getRandomNumber(-1, 1);
            int l = this.getRandomNumber(-3, 3);
            boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    private boolean tryToTeleportToLocation(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.balloon.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.balloon.getYRot(), this.balloon.getXRot());
            Entity hand = this.balloon.getChild();
            if(hand != null){
                hand.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.balloon.getYRot(), this.balloon.getXRot());
            }
            return true;
        }
    }

    private boolean isTeleportFriendlyBlock(BlockPos pos) {
        if(this.world.getBlockState(pos).isAir()){
            BlockPos blockpos = pos.subtract(this.balloon.blockPosition());
            return this.world.noCollision(this.balloon, this.balloon.getBoundingBox().move(blockpos));
        }
        NodeEvaluator nodeevaluator = balloon.getNavigation().getNodeEvaluator();
        BlockPathTypes pathnodetype = nodeevaluator.getBlockPathType(this.world, pos.getX(), pos.getY(), pos.getZ());
        if (pathnodetype != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.world.getBlockState(pos.below());
            if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.balloon.blockPosition());
                return this.world.noCollision(this.balloon, this.balloon.getBoundingBox().move(blockpos));
            }
        }
    }

    private int getRandomNumber(int min, int max) {
        return this.balloon.getRandom().nextInt(max - min + 1) + min;
    }
}

