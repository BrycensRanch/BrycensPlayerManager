
package me.brycensranch.BrycensPlayerManager.common.config;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.brycensranch.BrycensPlayerManager.common.BrycensPlayerManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import me.brycensranch.BrycensPlayerManager.common.util.Pair;

import static me.brycensranch.BrycensPlayerManager.common.util.Pair.pair;

public final class ConfigManager {

  private final BrycensPlayerManager<?> brycensPlayerManager;

  private final ConfigLoader<BPMConfig> mainConfigLoader;
  private BPMConfig mainConfig;

  private final ConfigLoader<PluginSettings> pluginSettingsLoader;
  private PluginSettings pluginSettings;

  private final Map<String, BPMConfig> extraConfigs = new HashMap<>();

  public ConfigManager(final @NonNull BrycensPlayerManager<?> brycensPlayerManager) {
    this.brycensPlayerManager = brycensPlayerManager;
    this.mainConfigLoader = new ConfigLoader<>(
      BPMConfig.class,
      this.brycensPlayerManager.dataDirectory().resolve("main.conf"),
      options -> options.header("BrycensPlayerManager Main Configuration")
    );
    this.pluginSettingsLoader = new ConfigLoader<>(
      PluginSettings.class,
      this.brycensPlayerManager.dataDirectory().resolve("plugin_settings.conf"),
      options -> options.header("BrycensPlayerManager Plugin Configuration")
    );
  }

  public void loadConfigs() {
    try {
      this.mainConfig = this.mainConfigLoader.load();
      this.mainConfigLoader.save(this.mainConfig);

      this.pluginSettings = this.pluginSettingsLoader.load();
      this.pluginSettingsLoader.save(this.pluginSettings);
    } catch (final ConfigurateException e) {
      throw new IllegalStateException("Failed to load config", e);
    }
  }

  public void loadExtraConfigs() {
    this.extraConfigs.clear();
    final Path extraConfigsDir = this.brycensPlayerManager.dataDirectory().resolve("extra-configs");
    try {
      if (!Files.exists(extraConfigsDir)) {
        Files.createDirectories(extraConfigsDir);
        this.createDefaultExtraConfigs(extraConfigsDir);
      }
      try (final Stream<Path> stream = Files.list(extraConfigsDir)) {
        for (final Path path : stream.collect(Collectors.toList())) {
          if (!path.toString().endsWith(".conf")) {
            continue;
          }
          final String name = path.getFileName().toString().replace(".conf", "");
          final ConfigLoader<BPMConfig> loader = new ConfigLoader<>(
            BPMConfig.class,
            path,
            options -> options.header(String.format("Extra BrycensPlayerManager config '%s'", name))
          );
          final BPMConfig config = loader.load();
          loader.save(config);
          this.extraConfigs.put(name, config);
        }
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Failed to load virtual host configs", e);
    }
  }

  private void createDefaultExtraConfigs(final @NonNull Path extraConfigsDir) throws ConfigurateException {
    final List<Pair<Path, BPMConfig.MOTD>> defaults = ImmutableList.of(
      pair(extraConfigsDir.resolve("skyblock.conf"), new BPMConfig.MOTD("<green><italic>Skyblock</green>", "<bold><rainbow>BrycensPlayerManager Skyblock Server")),
      pair(extraConfigsDir.resolve("survival.conf"), new BPMConfig.MOTD("<gradient:blue:red>Survival Mode Hardcore", "<green><bold>BrycensPlayerManager Survival Server"))
    );
    for (final Pair<Path, BPMConfig.MOTD> pair : defaults) {
      final ConfigLoader<BPMConfig> loader = new ConfigLoader<>(
        BPMConfig.class,
        pair.left()
      );
      loader.save(new BPMConfig(pair.right()));
    }
  }

  public @NonNull BPMConfig mainConfig() {
    if (this.mainConfig == null) {
      throw new IllegalStateException("Config has not yet been loaded");
    }
    return this.mainConfig;
  }

  public @NonNull PluginSettings pluginSettings() {
    if (this.pluginSettings == null) {
      throw new IllegalStateException("Config has not yet been loaded");
    }
    return this.pluginSettings;
  }

  public @NonNull BPMConfig resolveConfig(final @Nullable InetSocketAddress address) {
    if (address == null) {
      return this.mainConfig();
    }
    final String hostString = address.getHostString() + ":" + address.getPort();
    final String configString = this.pluginSettings().proxySettings().findConfigStringForHost(hostString);

    if (this.pluginSettings().proxySettings().virtualHostTestMode()) {
      this.brycensPlayerManager.platform().logger().info("[virtual-host-debug] Virtual Host: '{}', Selected Config: '{}'", hostString, configString == null ? "default" : configString);
    }

    if (configString == null) {
      return this.mainConfig();
    }
    return this.resolveConfig(configString);
  }

  public @NonNull BPMConfig resolveConfig(final @NonNull String name) {
    if ("default".equals(name)) {
      return this.mainConfig();
    }
    final BPMConfig cfg = this.extraConfigs.get(name);
    if (cfg != null) {
      return cfg;
    }
    this.brycensPlayerManager.logger().warn("Invalid extra-config name: '{}', falling back to main.conf", name);
    return this.mainConfig();
  }

}
