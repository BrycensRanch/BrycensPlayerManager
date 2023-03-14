
package xyz.jpenilla.minimotd.fabric.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.BrycensPlayerManager;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.BPMConfig;
import xyz.jpenilla.minimotd.common.util.ComponentColorDownsampler;
import xyz.jpenilla.minimotd.fabric.BrycensPlayerManagerFabric;
import xyz.jpenilla.minimotd.fabric.access.ConnectionAccess;

@Mixin(ServerStatusPacketListenerImpl.class)
abstract class ServerStatusPacketListenerImplMixin {
  @Shadow @Final private Connection connection;

  @Redirect(method = "handleStatusRequest", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
  public ServerStatus injectHandleStatusRequest(final MinecraftServer minecraftServer) {
    final ServerStatus vanillaStatus = minecraftServer.getStatus();

    final ServerStatus modifiedStatus = new ServerStatus();
    modifiedStatus.setDescription(vanillaStatus.getDescription());
    modifiedStatus.setFavicon(vanillaStatus.getFavicon());
    modifiedStatus.setVersion(vanillaStatus.getVersion());

    final BrycensPlayerManagerFabric brycensPlayerManagerFabric = BrycensPlayerManagerFabric.get();
    final BrycensPlayerManager<String> brycensPlayerManager = brycensPlayerManagerFabric.brycensPlayerManager();
    final BPMConfig config = brycensPlayerManager.configManager().mainConfig();

    final PingResponse<String> response = brycensPlayerManager.createMOTD(config, minecraftServer.getPlayerCount(), vanillaStatus.getPlayers().getMaxPlayers());

    response.motd(motd -> {
      if (((ConnectionAccess) this.connection).protocolVersion() >= Constants.MINECRAFT_1_16_PROTOCOL_VERSION) {
        modifiedStatus.setDescription(brycensPlayerManagerFabric.audiences().toNative(motd));
      } else {
        modifiedStatus.setDescription(brycensPlayerManagerFabric.audiences().toNative(ComponentColorDownsampler.downsampler().downsample(motd)));
      }
    });
    response.icon(modifiedStatus::setFavicon);

    if (!response.hidePlayerCount()) {
      final GameProfile[] oldSample = vanillaStatus.getPlayers().getSample();
      final ServerStatus.Players newPlayers = new ServerStatus.Players(response.playerCount().maxPlayers(), response.playerCount().onlinePlayers());
      if (response.disablePlayerListHover()) {
        newPlayers.setSample(new GameProfile[]{});
      } else {
        newPlayers.setSample(oldSample);
      }
      modifiedStatus.setPlayers(newPlayers);
    }

    return modifiedStatus;
  }
}
