package com.github.alexthe668.cloudstorage.mixin;

import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.block.CloudBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {

    @Inject(
            method = {"clearCopperOnLightningStrike"},
            at = @At("TAIL")
    )
    private static void chargeCloudBlocks(Level level, BlockPos pos, CallbackInfo ci) {
        BlockState blockstate = level.getBlockState(pos);
        BlockPos blockpos;
        BlockState blockstate1;
        if (blockstate.is(Blocks.LIGHTNING_ROD)) {
            blockpos = pos.relative(blockstate.getValue(LightningRodBlock.FACING).getOpposite());
            blockstate1 = level.getBlockState(blockpos);
        } else {
            blockpos = pos;
            blockstate1 = blockstate;
        }

        if (blockstate1.getBlock() instanceof CloudBlock) {
            level.setBlockAndUpdate(blockpos, CSBlockRegistry.STATIC_CLOUD.get().defaultBlockState());
            BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();
            int i = level.random.nextInt(3) + 3;

            for(int j = 0; j < i; ++j) {
                int k = level.random.nextInt(8) + 1;
                chargeRandomCloudsNearby(level, blockpos, blockpos$mutableblockpos, k);
            }

        }
    }

    private static void chargeRandomCloudsNearby(Level level, BlockPos blockpos, BlockPos.MutableBlockPos blockpos$mutableblockpos, int k) {
        blockpos$mutableblockpos.set(blockpos);

        for(int i = 0; i < k; ++i) {
            Optional<BlockPos> optional = chargeRandomCloud(level, blockpos$mutableblockpos);
            if (!optional.isPresent()) {
                break;
            }

            blockpos$mutableblockpos.set(optional.get());
        }
    }

    private static Optional<BlockPos> chargeRandomCloud(Level level, BlockPos blockpos2) {
        for(BlockPos blockpos : BlockPos.randomInCube(level.random, 10, blockpos2, 1)) {
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.getBlock() instanceof CloudBlock) {
                level.setBlockAndUpdate(blockpos, CSBlockRegistry.STATIC_CLOUD.get().defaultBlockState());
                level.levelEvent(3002, blockpos, -1);
                return Optional.of(blockpos);
            }
        }

        return Optional.empty();
    }
}
