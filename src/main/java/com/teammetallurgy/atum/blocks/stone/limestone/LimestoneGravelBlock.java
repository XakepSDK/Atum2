package com.teammetallurgy.atum.blocks.stone.limestone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GravelBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;

public class LimestoneGravelBlock extends GravelBlock {

    public LimestoneGravelBlock() {
        super(Block.Properties.create(Material.SAND).hardnessAndResistance(0.6F).sound(SoundType.GROUND).harvestTool(ToolType.SHOVEL).harvestLevel(0));
    }

    @Override
    public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull IBlockReader world, BlockPos pos, @Nonnull Direction facing, IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(world, pos.offset(facing));
        return plantType == PlantType.DESERT;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getDustColor(@Nonnull BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos) {
        return -2370656;
    }
}