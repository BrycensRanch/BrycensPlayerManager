
package xyz.jpenilla.minimotd.bukkit;

import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.BrycensPlayerManager;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.BPMConfig;

public final class PingListener implements Listener {
  private final BrycensPlayerManager<CachedServerIcon> brycensPlayerManager;
  private final LegacyComponentSerializer unusualHexSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

  PingListener(final @NonNull BrycensPlayerManager<CachedServerIcon> brycensPlayerManager) {
    this.brycensPlayerManager = brycensPlayerManager;
  }

  @EventHandler
  public void handlePing(final @NonNull ServerListPingEvent event) {
    final BPMConfig cfg = this.brycensPlayerManager.configManager().mainConfig();

    final PingResponse<CachedServerIcon> response = this.brycensPlayerManager.createMOTD(cfg, event.getNumPlayers(), event.getMaxPlayers());

    event.setMaxPlayers(response.playerCount().maxPlayers());
    response.motd(motd -> {
      if (PaperLib.getMinecraftVersion() > 15) {
        event.setMotd(this.unusualHexSerializer.serialize(motd));
      } else {
        event.setMotd(LegacyComponentSerializer.legacySection().serialize(motd));
      }
    });
    response.icon(icon -> {
      try {
        event.setServerIcon(icon);
      } catch (final UnsupportedOperationException ignore) {
      }
    });
  }
}
