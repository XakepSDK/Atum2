package com.teammetallurgy.atum.blocks.vegetation;

import com.teammetallurgy.atum.init.AtumItems;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.Random;

public class DateBlock extends BushBlock implements IGrowable {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
    private static final VoxelShape STEM = Block.makeCuboidShape(0.4125D, 0.625D, 0.4125D, 0.6D, 1.0D, 0.6D);
    private static final VoxelShape BOUNDING_BOX = Block.makeCuboidShape(0.3125D, 0.125D, 0.3125D, 0.6875D, 1.0D, 0.6875D);

    public DateBlock() {
        super(Properties.create(Material.PLANTS).sound(SoundType.PLANT).tickRandomly());
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE, 0));
    }

    @Override
    public float getBlockHardness(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.get(AGE) != 3) {
            return 0.25F;
        }
        return 0.35F;
    }

    @Override
    @Nonnull
    public VoxelShape getRenderShape(BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos) {
        if (state.get(AGE) == 0) {
            return STEM;
        }
        return BOUNDING_BOX;
    }

    @Override
    public void tick(BlockState state, World world, BlockPos pos, Random rand) {
        if (!world.isRemote) {
            super.tick(state, world, pos, rand);
            if (!world.isAreaLoaded(pos, 1)) return;
            if (state.get(AGE) != 7) {
                if (ForgeHooks.onCropsGrowPre(world, pos, state, world.rand.nextDouble() <= 0.12F)) {
                    world.setBlockState(pos, state.cycle(AGE), 2);
                    ForgeHooks.onCropsGrowPost(world, pos, state);
                }
            }
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        if (state.getBlock() == this) {
            BlockState stateUp = world.getBlockState(pos.up());
            return stateUp.getBlock().isIn(BlockTags.LEAVES);
        }
        return super.isValidGround(world.getBlockState(pos.down()), world, pos);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult rayTraceResult) {
        if (state.get(AGE) == 7) {
            spawnDrops(state, world, pos);
            return world.setBlockState(pos, this.getDefaultState());
        }
        return false;
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(AtumItems.DATE);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> container) {
        container.add(AGE);
    }

    @Override
    public boolean canGrow(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isClient) {
        return state.get(AGE) != 7;
    }

    @Override
    public boolean canUseBonemeal(@Nonnull World world, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return true;
    }

    @Override
    public void grow(@Nonnull World world, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        int growth = state.get(AGE) + MathHelper.nextInt(rand, 1, 2);
        int maxAge = 7;

        if (growth > maxAge) {
            growth = maxAge;
        }

        if (state.get(AGE) != 7) {
            world.setBlockState(pos, this.getDefaultState().with(AGE, growth), 2);
        }
    }
}