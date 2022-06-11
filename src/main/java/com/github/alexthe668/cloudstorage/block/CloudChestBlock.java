package com.github.alexthe668.cloudstorage.block;

import com.github.alexthe668.cloudstorage.CloudStorage;
import com.github.alexthe668.cloudstorage.client.particle.CSParticleRegistry;
import com.github.alexthe668.cloudstorage.network.MessageOpenCloudChest;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class CloudChestBlock extends BaseEntityBlock {

    public static final DirectionProperty HORIZONTAL_FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private final boolean lightning;

    public CloudChestBlock(boolean lightning) {
        super(Properties.of(Material.GLASS).noOcclusion().sound(SoundType.WOOL).strength(1.5F));
        this.lightning = lightning;
    }

    public VoxelShape getShape(BlockState p_51569_, BlockGetter p_51570_, BlockPos p_51571_, CollisionContext p_51572_) {
        return AABB;
    }

    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return p_185499_1_.setValue(HORIZONTAL_FACING, p_185499_2_.rotate(p_185499_1_.getValue(HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(HORIZONTAL_FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
        return p_200122_2_.getBlock() == this || super.skipRendering(p_200122_1_, p_200122_2_, p_200122_3_);
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        if (worldIn.getBlockEntity(pos) instanceof AbstractCloudChestBlockEntity chest) {
            if (!chest.hasClearance()) {
                player.displayClientMessage(Component.translatable("message.cloudstorage.no_sky_access").withStyle(ChatFormatting.RED), true);
                return InteractionResult.PASS;
            } else if (!chest.hasBalloonFor(player)) {
                player.displayClientMessage(Component.translatable("message.cloudstorage.no_balloon").withStyle(ChatFormatting.RED), true);
                return InteractionResult.PASS;
            }
            if (worldIn.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                CloudStorage.sendMSGToAll(new MessageOpenCloudChest(chest.getContainerSize(player)));
                if (chest.hasNoInvSpace(player)) {
                    player.displayClientMessage(Component.translatable("message.cloudstorage.no_inventory_space").withStyle(ChatFormatting.RED), true);
                } else {
                    MenuProvider menuprovider = chest.getMenuProvider();
                    if (menuprovider != null) {
                        player.openMenu(menuprovider);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (worldIn.getBlockEntity(pos) instanceof AbstractCloudChestBlockEntity cloudChest) {
            cloudChest.releaseBalloons();
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return lightning ? new StaticCloudChestBlockEntity(pos, state) : new CloudChestBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        if (lightning) {
            return createTickerHelper(p_152182_, CSBlockEntityRegistry.STATIC_CLOUD_CHEST.get(), StaticCloudChestBlockEntity::commonTick);
        } else {
            return createTickerHelper(p_152182_, CSBlockEntityRegistry.CLOUD_CHEST.get(), CloudChestBlockEntity::commonTick);
        }
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rng) {
        if (lightning && rng.nextInt(1) == 0) {
            Direction direction = Direction.getRandom(rng);
            BlockPos blockpos = pos.relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            if (!state.canOcclude() || !blockstate.isFaceSturdy(level, blockpos, direction.getOpposite())) {
                double d0 = direction.getStepX() == 0 ? rng.nextDouble() - 0.5D : (double) direction.getStepX() * 0.3D;
                double d1 = direction.getStepY() == 0 ? rng.nextDouble() - 0.5D : (double) direction.getStepY() * 0.3D;
                double d2 = direction.getStepZ() == 0 ? rng.nextDouble() - 0.5D : (double) direction.getStepZ() * 0.3D;
                double length = 0.3D + rng.nextFloat() * 0.3D;
                double d3 = d0 * length;
                double d4 = d1 * length;
                double d5 = d2 * length;
                level.addParticle(CSParticleRegistry.STATIC_LIGHTNING.get(), (double) pos.getX() + 0.5D + d0, (double) pos.getY() + 0.5D + d1, (double) pos.getZ() + 0.5D + d2, d3, d4, d5);
            }
        }
    }
}
