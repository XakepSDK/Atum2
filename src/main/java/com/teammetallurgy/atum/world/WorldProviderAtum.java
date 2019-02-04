package com.teammetallurgy.atum.world;

import com.teammetallurgy.atum.blocks.BlockSandLayers;
import com.teammetallurgy.atum.init.AtumBlocks;
import com.teammetallurgy.atum.network.NetworkHandler;
import com.teammetallurgy.atum.network.packet.PacketStormStrength;
import com.teammetallurgy.atum.network.packet.PacketWeather;
import com.teammetallurgy.atum.world.biome.base.AtumBiomeProvider;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.Random;

import javax.annotation.Nonnull;

public class WorldProviderAtum extends WorldProvider {
    public boolean hasStartStructureSpawned;

    @Override
    @Nonnull
    public DimensionType getDimensionType() {
        return AtumDimension.ATUM;
    }

    @Override
    protected void init() {
        this.hasSkyLight = true;
        this.biomeProvider = new AtumBiomeProvider(world.getWorldInfo());
        NBTTagCompound tagCompound = this.world.getWorldInfo().getDimensionData(this.world.provider.getDimension());
        this.hasStartStructureSpawned = this.world instanceof WorldServer && tagCompound.getBoolean("HasStartStructureSpawned");
        this.isStorming = this.world instanceof WorldServer && tagCompound.getBoolean("IsStorming");
    }

    @Override
    @Nonnull
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorAtum(world, world.getSeed(), true, world.getWorldInfo().getGeneratorOptions());
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        BlockPos pos = new BlockPos(x, 0, z);

        if (this.world.getBiome(pos).ignorePlayerSpawnSuitability()) {
            return true;
        } else {
            return this.world.getGroundAboveSeaLevel(pos).getBlock() == AtumBlocks.SAND;
        }
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float par1, float par2) {
        float f = MathHelper.cos(par1 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
        if (f < 0.2F) {
            f = 0.2F;
        }

        if (f > 1.0F) {
            f = 1.0F;
        }

        // Darken fog as sandstorm builds
        // f *= (1 - this.stormStrength) * 0.8 + 0.2;

        float f1 = 0.9F * f;
        float f2 = 0.75F * f;
        float f3 = 0.6F * f;
        return new Vec3d((double) f1, (double) f2, (double) f3);
    }

    @Override
    public void onWorldSave() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setBoolean("HasStartStructureSpawned", hasStartStructureSpawned);
        tagCompound.setBoolean("IsStorming", isStorming);
        world.getWorldInfo().setDimensionData(this.world.provider.getDimension(), tagCompound);
    }

    public boolean isStorming;
    public int stormTime;
    public float prevStormStrength;
    public float stormStrength;
    private int updateLCG = (new Random()).nextInt();
    
    @Override
    public void calculateInitialWeather() {
        super.calculateInitialWeather();
        if (isStorming) {
            stormStrength = 1;
        }
    }
    
    public boolean canPlaceSandAt(BlockPos pos) {
    	IBlockState state = world.getBlockState(pos);
    	if (state.getBlock() == AtumBlocks.SAND) {
    		return false;
    	}
    	if (state.getBlock().isReplaceable(world, pos)) {
            state = world.getBlockState(pos.down());
            BlockFaceShape blockfaceshape = state.getBlockFaceShape(world, pos.down(), EnumFacing.UP);
        	if (blockfaceshape == BlockFaceShape.SOLID || state.getBlock().isLeaves(state, world, pos.down())) {
        		return true;
        	}
    	}
    	return false;
    }

    @Override
    public void updateWeather()
    {
        //super.updateWeather();
        //System.out.println(isStorming + " " + stormTime);

        int cleanWeatherTime = world.getWorldInfo().getCleanWeatherTime();

        if (cleanWeatherTime > 0)
        {
            --cleanWeatherTime;
            world.getWorldInfo().setCleanWeatherTime(cleanWeatherTime);
            this.stormTime = isStorming ? 1 : 2;
        }
        
        if(stormTime % 20 == 0)
        	System.out.println("StormTime: " + stormTime);
        //stormTime = 60;
        
        if(stormTime <= 0) {
        	if(isStorming) {
        		stormTime = this.world.rand.nextInt(6000) + 6000;
        		System.out.println("New Storm: " + stormTime);
        	} else {
        		stormTime = this.world.rand.nextInt(168000) + 12000;
        		System.out.println("Next Storm: " + stormTime);
        	}
    		NetworkHandler.WRAPPER.sendToDimension(new PacketWeather(isStorming, stormTime), this.getDimension());
        } else {
            stormTime--;
            if (stormTime <= 0) {
                isStorming = !isStorming;
            }
        }

        prevStormStrength = stormStrength;
        if (isStorming) {
            stormStrength += 0.002f;
        } else {
            stormStrength -= 0.002f;
        }
        stormStrength = MathHelper.clamp(stormStrength, 0, 1);

        if (stormStrength != prevStormStrength) {
            NetworkHandler.WRAPPER.sendToDimension(new PacketStormStrength(stormStrength), this.getDimension());
        }
        
        if(!world.isRemote) {
        	Iterator<Chunk> iterator = world.getPersistentChunkIterable(((WorldServer)world).getPlayerChunkMap().getChunkIterator());
	        while (iterator.hasNext()) {
                Chunk chunk = iterator.next();
                int j = chunk.x * 16;
                int k = chunk.z * 16;
                
		        if (world.rand.nextInt(16) == 0) {
		            this.updateLCG = this.updateLCG * 3 + 1013904223;
		            int j2 = this.updateLCG >> 2;
		            BlockPos blockpos1 = world.getPrecipitationHeight(new BlockPos(j + (j2 & 15), 0, k + (j2 >> 8 & 15)));
		            BlockPos blockpos2 = blockpos1.down();
		            
		            if (world.isAreaLoaded(blockpos2, 1)) {// Forge: check area to avoid loading neighbors in unloaded chunks
		            	IBlockState sandState = world.getBlockState(blockpos1);
		            	if (stormStrength > 0.9f) {
			            	if (sandState.getBlock() == AtumBlocks.SAND_LAYERED) {
			            		int layers = sandState.getValue(BlockSandLayers.LAYERS);
			            		if (layers < 3) {
			            			world.setBlockState(blockpos1, sandState.withProperty(BlockSandLayers.LAYERS, ++layers));
			            		}
			            	} else if (canPlaceSandAt(blockpos1)) {
				            	world.setBlockState(blockpos1, AtumBlocks.SAND_LAYERED.getDefaultState());
			            	}
		            	}
		            }
		        }
	        }
        }
    }
}
