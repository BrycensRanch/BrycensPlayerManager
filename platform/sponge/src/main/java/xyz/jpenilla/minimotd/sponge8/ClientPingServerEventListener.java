
package xyz.jpenilla.minimotd.sponge8;

import com.google.inject.Inject;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.BrycensPlayerManager;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.BPMConfig;
import xyz.jpenilla.minimotd.common.util.ComponentColorDownsampler;

final class ClientPingServerEventListener implements EventListener<ClientPingServerEvent> {
  private static final Method GET_PROTOCOL;

  static {
    try {
      final Class<?> protocolMinecraftVersion = Class.forName("org.spongepowered.common.ProtocolMinecraftVersion");
      GET_PROTOCOL = protocolMinecraftVersion.getMethod("getProtocol");
    } catch (final ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }

  private final BrycensPlayerManager<Favicon> brycensPlayerManager;

  @Inject
  private ClientPingServerEventListener(final @NonNull BrycensPlayerManager<Favicon> brycensPlayerManager) {
    this.brycensPlayerManager = brycensPlayerManager;
  }

  @Override
  public void handle(final @NonNull ClientPingServerEvent event) {
    final ClientPingServerEvent.Response response = event.response();

    final ClientPingServerEvent.Response.Players players;
    final ClientPingServerEvent.Response.Players players0 = response.players().orElse(null);
    if (players0 != null) {
      players = players0;
    } else {
      response.setHidePlayers(false);
      players = response.players().orElse(null);
      if (players == null) {
        this.brycensPlayerManager.logger().warn(String.format("Failed to handle ClientPingServerEvent: '%s', response.players() was null.", event));
        return;
      }
    }

    final BPMConfig config = this.brycensPlayerManager.configManager().mainConfig();

    final PingResponse<Favicon> mini = this.brycensPlayerManager.createMOTD(config, players.online(), players.max());
    mini.playerCount().applyCount(players::setOnline, players::setMax);
    mini.motd(motd -> {
      if (this.legacy(event.client().version())) {
        response.setDescription(ComponentColorDownsampler.downsampler().downsample(motd));
      } else {
        response.setDescription(motd);
      }
    });
    mini.icon(response::setFavicon);

    if (mini.disablePlayerListHover()) {
      players.profiles().clear();
    }
    if (mini.hidePlayerCount()) {
      response.setHidePlayers(true);
    }
  }

  private boolean legacy(final @NonNull MinecraftVersion version) {
    try {
      return version.isLegacy()
        || (int) GET_PROTOCOL.invoke(version) < Constants.MINECRAFT_1_16_PROTOCOL_VERSION;
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to get protocol version", e);
    }
  }
}
