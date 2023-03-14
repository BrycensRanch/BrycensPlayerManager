
package xyz.jpenilla.minimotd.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.minimotd.common.BrycensPlayerManager;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.BPMConfig;

@DefaultQualifier(NonNull.class)
public final class PingListener {
  private final BrycensPlayerManager<Favicon> brycensPlayerManager;

  @Inject
  private PingListener(final BrycensPlayerManager<Favicon> brycensPlayerManager) {
    this.brycensPlayerManager = brycensPlayerManager;
  }

  @Subscribe
  public EventTask onProxyPingEvent(final ProxyPingEvent event) {
    return EventTask.async(() -> this.handle(event));
  }

  private void handle(final ProxyPingEvent event) {
    final BPMConfig config = this.brycensPlayerManager.configManager().resolveConfig(event.getConnection().getVirtualHost().orElse(null));
    final ServerPing.Builder pong = event.getPing().asBuilder();

    final PingResponse<Favicon> response = this.brycensPlayerManager.createMOTD(config, pong.getOnlinePlayers(), pong.getMaximumPlayers());
    response.icon(pong::favicon);
    response.motd(pong::description);
    response.playerCount().applyCount(pong::onlinePlayers, pong::maximumPlayers);

    if (response.disablePlayerListHover()) {
      pong.clearSamplePlayers();
    }
    if (response.hidePlayerCount()) {
      pong.nullPlayers();
    }

    event.setPing(pong.build());
  }
}
