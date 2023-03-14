
package xyz.jpenilla.minimotd.bungee;

import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.BrycensPlayerManager;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.BPMConfig;

public final class PingListener implements Listener {
  private final BrycensPlayerManager<Favicon> brycensPlayerManager;

  PingListener(final @NonNull BrycensPlayerManager<Favicon> brycensPlayerManager) {
    this.brycensPlayerManager = brycensPlayerManager;
  }

  @EventHandler
  public void onPing(final @NonNull ProxyPingEvent e) {
    final ServerPing response = e.getResponse();
    if (response == null) {
      return;
    }

    final ServerPing.Players players = response.getPlayers();
    final BPMConfig cfg = this.brycensPlayerManager.configManager().resolveConfig(e.getConnection().getVirtualHost());
    final PingResponse<Favicon> mini = this.brycensPlayerManager.createMOTD(cfg, players.getOnline(), players.getMax());

    if (mini.hidePlayerCount()) {
      response.setPlayers(null);
    } else {
      mini.playerCount().applyCount(players::setOnline, players::setMax);
      if (mini.disablePlayerListHover()) {
        players.setSample(new ServerPing.PlayerInfo[]{});
      }
    }

    mini.motd(motd -> {
      final BaseComponent[] bungee;
      if (e.getConnection().getVersion() < Constants.MINECRAFT_1_16_PROTOCOL_VERSION) {
        bungee = BungeeComponentSerializer.legacy().serialize(motd);
      } else {
        bungee = BungeeComponentSerializer.get().serialize(motd);
      }
      if (BungeeComponentSerializer.isNative()) {
        response.setDescriptionComponent(bungee[0]);
      } else {
        response.setDescriptionComponent(new TextComponent(bungee));
      }
    });
    mini.icon(response::setFavicon);

    e.setResponse(response);
  }
}
