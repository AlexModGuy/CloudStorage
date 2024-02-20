package com.github.alexthe668.cloudstorage.block;

import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CloudBlock extends Block {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private boolean lightning;

    public CloudBlock(boolean lightning, Properties properties) {
        super(properties);
        this.lightning = lightning;
    }

    public boolean skipRendering(BlockState state, BlockState otherState, Direction direction) {
        return otherState.getBlock() instanceof CloudBlock ? true : super.skipRendering(state, otherState, direction);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter lvl, BlockPos pos, CollisionContext context) {
        return lvl.getBlockState(pos.above()).getBlock() instanceof CloudBlock ? Shapes.block() : SHAPE;
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter lvl, BlockPos pos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter lvl, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    public float getJumpFactor() {
        return super.getJumpFactor() + 0.5F;
    }

    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float dist) {
        entity.causeFallDamage(dist, 0.1F, entity.damageSources().fall());
    }


    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(blockGetter, entity);
        } else {
            this.bounceUp(entity);
        }

    }

    private void bounceUp(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0D) {
            double d0 = entity instanceof LivingEntity ? 0.5D : 0.2D;
            entity.setDeltaMovement(vec3.x, -vec3.y * d0, vec3.z);
        }
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return 0.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rng) {
        if (lightning && rng.nextInt(1) == 0) {
            Direction direction = Direction.getRandom(rng);
            BlockPos blockpos = pos.relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            if ((!state.canOcclude() || !blockstate.isFaceSturdy(level, blockpos, direction.getOpposite())) && !(blockstate.getBlock() instanceof CloudBlock)) {
                double d0 = direction.getStepX() == 0 ? rng.nextDouble() - 0.5D : (double)direction.getStepX() * 0.4D;
                double d1 = direction.getStepY() == 0 ? rng.nextDouble() - 0.5D : (double)direction.getStepY() * 0.4D;
                double d2 = direction.getStepZ() == 0 ? rng.nextDouble() - 0.5D : (double)direction.getStepZ() * 0.4D;
                double length = 0.2D + rng.nextFloat() * 0.2D;
                double d3 = d0 * length;
                double d4 = d1 * length;
                double d5 = d2 * length;
                level.addParticle(CSParticleRegistry.STATIC_LIGHTNING.get(), (double)pos.getX() + 0.5D + d0, (double)pos.getY() + 0.5D + d1, (double)pos.getZ() + 0.5D + d2, d3, d4, d5);
            }
        }
    }
}
