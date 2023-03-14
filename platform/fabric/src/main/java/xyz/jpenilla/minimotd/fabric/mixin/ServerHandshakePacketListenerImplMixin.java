
package xyz.jpenilla.minimotd.fabric.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.jpenilla.minimotd.fabric.access.ConnectionAccess;

@Mixin(ServerHandshakePacketListenerImpl.class)
abstract class ServerHandshakePacketListenerImplMixin {
  @Shadow @Final private Connection connection;

  @Inject(method = "handleIntention", at = @At("HEAD"))
  public void injectHandleIntention(final ClientIntentionPacket packet, final CallbackInfo ci) {
    ((ConnectionAccess) this.connection).protocolVersion(packet.getProtocolVersion());
  }
}
