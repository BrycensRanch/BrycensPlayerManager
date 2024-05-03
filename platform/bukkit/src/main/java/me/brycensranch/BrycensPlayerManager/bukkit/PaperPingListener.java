
package me.brycensranch.BrycensPlayerManager.bukkit;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import me.brycensranch.BrycensPlayerManager.common.Constants;
import me.brycensranch.BrycensPlayerManager.common.BrycensPlayerManager;
import me.brycensranch.BrycensPlayerManager.common.PingResponse;
import me.brycensranch.BrycensPlayerManager.common.config.BPMConfig;

public final class PaperPingListener implements Listener {
  private final LegacyComponentSerializer unusualHexSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
  private final BrycensPlayerManager<CachedServerIcon> brycensPlayerManager;

  PaperPingListener(final @NonNull BrycensPlayerManager<CachedServerIcon> brycensPlayerManager) {
    this.brycensPlayerManager = brycensPlayerManager;
  }

  @EventHandler
  public void handlePing(final @NonNull PaperServerListPingEvent event) {
    final BPMConfig cfg = this.brycensPlayerManager.configManager().mainConfig();

    final PingResponse<CachedServerIcon> response = this.brycensPlayerManager.createMOTD(cfg, event.getNumPlayers(), event.getMaxPlayers());

    response.playerCount().applyCount(event::setNumPlayers, event::setMaxPlayers);
    response.motd(motd -> {
      if (event.getClient().getProtocolVersion() < Constants.MINECRAFT_1_16_PROTOCOL_VERSION || PaperLib.getMinecraftVersion() < 16) {
        event.setMotd(LegacyComponentSerializer.legacySection().serialize(motd));
      } else {
        event.setMotd(this.unusualHexSerializer.serialize(motd));
      }
    });
    response.icon(event::setServerIcon);

    if (response.disablePlayerListHover()) {
      event.getPlayerSample().clear();
    }
    if (response.hidePlayerCount()) {
      event.setHidePlayers(true);
    }
  }
}
