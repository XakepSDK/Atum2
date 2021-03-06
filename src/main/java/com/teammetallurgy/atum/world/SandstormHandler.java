package com.teammetallurgy.atum.world;

import com.teammetallurgy.atum.Atum;
import com.teammetallurgy.atum.blocks.SandLayersBlock;
import com.teammetallurgy.atum.init.AtumBlocks;
import com.teammetallurgy.atum.misc.AtumConfig;
import com.teammetallurgy.atum.network.NetworkHandler;
import com.teammetallurgy.atum.network.packet.StormStrengthPacket;
import com.teammetallurgy.atum.network.packet.WeatherPacket;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

public class SandstormHandler {
    public static final SandstormHandler INSTANCE = new SandstormHandler();
    public int stormTime;
    public float prevStormStrength;
    public float stormStrength;
    private long lastUpdateTime;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onWorldLoad(WorldEvent.Load event) { // calculateInitialWeather
        if (event.getWorld() instanceof ServerWorld && DimensionHelper.getData((ServerWorld) event.getWorld()).isStorming()) {
            this.stormStrength = 1.0F;
        }
    }

    @SubscribeEvent
    public void onPreServerTick(TickEvent.WorldTickEvent event) {
        if (event.world.getDimensionKey() == Atum.ATUM) {
            updateWeather(event.world);
        }
    }

    private boolean canPlaceSandAt(ServerWorld serverWorld, BlockPos pos) {
        BlockState state = serverWorld.getBlockState(pos.down());
        return ((state.getBlock() != AtumBlocks.SAND && state.getBlock() != AtumBlocks.LIMESTONE_GRAVEL) || state.getBlock().isIn(BlockTags.LEAVES)) && DimensionHelper.canPlaceSandLayer(serverWorld, pos);
    }

    public void updateWeather(World world) {
        if (world instanceof ServerWorld && !world.isRemote) {
            ServerWorld serverWorld = (ServerWorld) world;
            IServerWorldInfo worldInfo = serverWorld.getServer().getServerConfiguration().getServerWorldInfo();
            int cleanWeatherTime = worldInfo.getClearWeatherTime();

            if (cleanWeatherTime > 0) {
                --cleanWeatherTime;
                this.stormTime = DimensionHelper.getData(serverWorld).isStorming() ? 1 : 2;
            }

            if (this.stormTime <= 0) {
                if (DimensionHelper.getData(serverWorld).isStorming()) {
                    this.stormTime = serverWorld.rand.nextInt(6000) + 6000;
                } else {
                    this.stormTime = serverWorld.rand.nextInt(168000) + 12000;
                }
                DimensionHelper.getData(serverWorld).setStorming(DimensionHelper.getData(serverWorld).isStorming());
                NetworkHandler.sendToDimension(new WeatherPacket(this.stormTime), serverWorld, Atum.ATUM);
            } else {
                this.stormTime--;
                if (this.stormTime <= 0) {
                    DimensionHelper.getData(serverWorld).setStorming(!DimensionHelper.getData(serverWorld).isStorming());
                }
            }

            worldInfo.setClearWeatherTime(cleanWeatherTime);

            this.prevStormStrength = this.stormStrength;
            if (DimensionHelper.getData(serverWorld).isStorming()) {
                this.stormStrength += 1.0F / (float) (20 * AtumConfig.SANDSTORM.sandstormTransitionTime.get());
            } else {
                this.stormStrength -= 1.0F / (float) (20 * AtumConfig.SANDSTORM.sandstormTransitionTime.get());
            }
            this.stormStrength = MathHelper.clamp(this.stormStrength, 0.0F, 1.0F);

            if (this.stormStrength != this.prevStormStrength || this.lastUpdateTime < System.currentTimeMillis() - 1000) {
                NetworkHandler.sendToDimension(new StormStrengthPacket(this.stormStrength), serverWorld, Atum.ATUM);
                this.lastUpdateTime = System.currentTimeMillis();
            }

            try {
                if (AtumConfig.SANDSTORM.sandstormSandLayerChance.get() > 0 && serverWorld.rand.nextInt(AtumConfig.SANDSTORM.sandstormSandLayerChance.get()) == 0) {
                    if (this.stormStrength > 0.9F) {
                        ChunkManager chunkManager = serverWorld.getWorldServer().getChunkProvider().chunkManager;

                        chunkManager.getLoadedChunksIterable().forEach(chunkHolder -> {
                            Optional<Chunk> optionalChunk = chunkHolder.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_CHUNK).left();
                            if (optionalChunk.isPresent()) {
                                ChunkPos chunkPos = optionalChunk.get().getPos();
                                if (!chunkManager.isOutsideSpawningRadius(chunkPos)) {
                                    BlockPos pos = serverWorld.getHeight(Heightmap.Type.MOTION_BLOCKING, serverWorld.getBlockRandomPos(chunkPos.getXStart(), 0, chunkPos.getZStart(), 15));
                                    BlockPos posDown = pos.down();

                                    if (serverWorld.isAreaLoaded(posDown, 1)) {
                                        BlockState sandState = serverWorld.getBlockState(pos);
                                        if (sandState.getBlock() == AtumBlocks.SAND_LAYERED) {
                                            int layers = sandState.get(SandLayersBlock.LAYERS);
                                            if (layers < 3) {
                                                serverWorld.setBlockState(pos, sandState.with(SandLayersBlock.LAYERS, ++layers));
                                            }
                                        } else if (this.canPlaceSandAt(serverWorld, pos)) {
                                            serverWorld.setBlockState(pos, AtumBlocks.SAND_LAYERED.getDefaultState());
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Atum.LOG.error("Error occurred while Sandstorm attempted to place Sand Layer");
            }
        }
    }
}