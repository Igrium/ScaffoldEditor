package com.igrium.scaffold.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.scaffold.engine.IServerWorldMixin;

import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ChunkDataSender;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ChunkDataSender.class)
public class ChunkDataSenderMixin {

    // For some reason, the networking code takes the chunk directly from
    // ThreadedAnvilStorage instead of what's loaded on the server, so we need to
    // force it to send the overwritten chunk data from the world.
    // TODO: find a better approach to this
    @Inject(method = "sendChunkData", at = @At("HEAD"), cancellable = true)
    private static void sendCustomChunkData(ServerPlayNetworkHandler handler, ServerWorld world, WorldChunk chunk,
            CallbackInfo ci) {
        if (((IServerWorldMixin) world).isEditorWorld()) {
            var chunkPos = chunk.getPos();
            handler.sendPacket(new ChunkDataS2CPacket(world.getChunk(chunkPos.x, chunkPos.z), world.getLightingProvider(), null, null));
            ci.cancel();
        }
    }
}
