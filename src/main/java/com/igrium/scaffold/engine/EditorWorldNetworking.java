package com.igrium.scaffold.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

/**
 * In vanilla, block update networking is handled by {@link ChunkHolder}.
 * Because we're not using it, this is a simplified implementation of the same
 * networking.
 */
public class EditorWorldNetworking {
    protected final ServerWorld world;

    protected Map<ChunkPos, ChunkNetworking> chunks = new HashMap<>();

    public EditorWorldNetworking(ServerWorld world) {
        this.world = world;
    }

    public void markForBlockUpdate(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkNetworking networking = chunks.get(chunkPos);

        if (networking == null) {
            WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
            if (chunk == null) return;
            networking = new ChunkNetworking(chunk);
            chunks.put(chunkPos, networking);
        }

        networking.markForBlockUpdate(pos);
    }

    public void syncChunks(Collection<ServerPlayerEntity> players) {
        for (ChunkNetworking networking : chunks.values()) {
            networking.networkToClients(players);
        }
    }

    // For now, we sync all chunks with all players.
    public void syncChunks() {
        syncChunks(world.getServer().getPlayerManager().getPlayerList());
    }

    private class ChunkNetworking {
        public final ShortSet[] blockUpdatesBySection = new ShortSet[world.countVerticalSections()];
        public final WorldChunk chunk;
        public boolean pendingBlockUpdates;

        public ChunkNetworking(WorldChunk chunk) {
            this.chunk = chunk;
        }

        /**
         * Adapted from {@link ChunkHolder#markForBlockUpdate}
         * @param pos
         */
        public void markForBlockUpdate(BlockPos pos) {
            int index = world.getSectionIndex(pos.getY());
            if (blockUpdatesBySection[index] == null) {
                pendingBlockUpdates = true;
                blockUpdatesBySection[index] = new ShortOpenHashSet();
            }

            blockUpdatesBySection[index].add(ChunkSectionPos.packLocal(pos));
        }

        public void networkToClients(Collection<ServerPlayerEntity> players) {
            if (!pendingBlockUpdates) return;

            for (int i = 0; i < blockUpdatesBySection.length; i++) {
                ShortSet shortSet = blockUpdatesBySection[i];
                if (shortSet == null) continue;
                blockUpdatesBySection[i] = null;

                if (players.isEmpty()) continue;

                int y = world.sectionIndexToCoord(i);
                ChunkSectionPos sectionPos = ChunkSectionPos.from(chunk.getPos(), y);

                if (shortSet.size() == 1) {
                    BlockPos blockPos = sectionPos.unpackBlockPos(shortSet.iterator().nextShort());
                    BlockState state = world.getBlockState(blockPos);
                    sendPacketToPlayers(players, new BlockUpdateS2CPacket(blockPos, state));
                } else {
                    ChunkSection section = chunk.getSection(i);
                    ChunkDeltaUpdateS2CPacket packet = new ChunkDeltaUpdateS2CPacket(sectionPos, shortSet, section);
                    sendPacketToPlayers(players, packet);
                }
            }
            pendingBlockUpdates = false;
        }

        private static void sendPacketToPlayers(Collection<ServerPlayerEntity> players, Packet<?> packet) {
            players.forEach(player -> player.networkHandler.sendPacket(packet));
        }
    }
}
