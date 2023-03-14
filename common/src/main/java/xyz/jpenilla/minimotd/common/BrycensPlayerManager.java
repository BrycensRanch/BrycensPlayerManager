
package xyz.jpenilla.minimotd.common;

import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.config.ConfigManager;
import xyz.jpenilla.minimotd.common.config.BPMConfig;
import xyz.jpenilla.minimotd.common.util.Components;

import static net.kyori.adventure.text.Component.newline;

@DefaultQualifier(NonNull.class)
public final class BrycensPlayerManager<I> {
  private final ConfigManager configManager;
  private final IconManager<I> iconManager;
  private final BPMPlatform<I> platform;

  public BrycensPlayerManager(final BPMPlatform<I> platform) {
    this.platform = platform;

    try {
      this.iconManager = new IconManager<>(this);
      this.configManager = new ConfigManager(this);
      this.configManager.loadConfigs();
    } catch (final Exception ex) {
      throw new IllegalStateException("Failed to load BrycensPlayerManager. Ensure your config files are without errors.", ex);
    }
  }

  public BPMPlatform<I> platform() {
    return this.platform;
  }

  public Path dataDirectory() {
    return this.platform.dataDirectory();
  }

  public IconManager<I> iconManager() {
    return this.iconManager;
  }

  public Logger logger() {
    return this.platform.logger();
  }

  public ConfigManager configManager() {
    return this.configManager;
  }

  public PingResponse<I> createMOTD(final BPMConfig config, final int onlinePlayers, final int maxPlayers) {
    final PingResponse.PlayerCount count = config.modifyPlayerCount(onlinePlayers, maxPlayers);
    final PingResponse.Builder<I> response = PingResponse.<I>builder()
      .playerCount(count)
      .disablePlayerListHover(config.disablePlayerListHover())
      .hidePlayerCount(config.hidePlayerCount());

    @Nullable String iconString = null;
    if (config.motdEnabled()) {
      if (config.motds().isEmpty()) {
        throw new IllegalStateException("MOTD is enabled, but there are no MOTDs in the config file?");
      }
      final int index = config.motds().size() == 1 ? 0 : ThreadLocalRandom.current().nextInt(config.motds().size());
      final BPMConfig.MOTD motdConfig = config.motds().get(index);
      final Component motd = Components.ofChildren(
        parse(motdConfig.line1(), count),
        newline(),
        parse(motdConfig.line2(), count)
      );
      response.motd(motd);
      iconString = motdConfig.icon();
    }

    if (config.iconEnabled()) {
      response.icon(this.iconManager().icon(iconString));
    }

    return response.build();
  }

  private static Component parse(final String input, final PingResponse.PlayerCount count) {
    final String online = Integer.toString(count.onlinePlayers());
    final String max = Integer.toString(count.maxPlayers());
    return MiniMessage.miniMessage().deserialize(
      replacePlayerCount(input, online, max),
      TagResolver.resolver(Placeholder.unparsed("online_players", online), Placeholder.unparsed("max_players", max))
    );
  }

  private static String replacePlayerCount(final String input, final String online, final String max) {
    return input.replace("{onlinePlayers}", online).replace("{maxPlayers}", max);
  }

  public void reload() {
    this.iconManager.loadIcons();
    this.configManager.loadConfigs();
    this.platform.onReload();
  }
}
