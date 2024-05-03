
package me.brycensranch.BrycensPlayerManager.common.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class PluginSettings {

  @Comment("Do you want the plugin to check for updates on GitHub at launch?\n"
    + "https://github.com/BrycensRanch/BrycensPlayerManager")
  private boolean updateChecker = true;

  @Comment("Settings only applicable when running the plugin on a proxy (Velocity or Waterfall/Bungeecord)")
  private ProxySettings proxySettings = new ProxySettings();

  @ConfigSerializable
  public static final class ProxySettings {

    public ProxySettings() {
      this.virtualHostConfigs.put("minigames.example.com:25565", "default");
      this.virtualHostConfigs.put("survival.example.com:25565", "survival");
      this.virtualHostConfigs.put("skyblock.example.com:25565", "skyblock");
    }

    @Comment("Here you can assign configs in the 'extra-configs' folder to specific virtual hosts\n"
      + "Either use the name of the config in 'extra-configs', or use \"default\" to use the configuration in main.conf\n"
      + "\n"
      + "Format is \"hostname:port\"=\"configName|default\"")
    private final Map<String, String> virtualHostConfigs = new HashMap<>();

    @Comment("Set whether to enable virtual host testing mode.\n"
      + "When enabled, BrycensPlayerManager will print virtual host debug info to the console on each server ping.")
    private boolean virtualHostTestMode = false;

    public boolean virtualHostTestMode() {
      return this.virtualHostTestMode;
    }

    public @Nullable String findConfigStringForHost(final @NonNull String host) {
      return this.virtualHostConfigs.get(host.toLowerCase(Locale.ENGLISH));
    }
  }

  public @NonNull ProxySettings proxySettings() {
    return this.proxySettings;
  }

  public boolean updateChecker() {
    return this.updateChecker;
  }

  @PostProcessor
  private void lowercaseVirtualHosts() {
    final Map<String, String> virtualHosts = new HashMap<>(this.proxySettings.virtualHostConfigs);
    this.proxySettings.virtualHostConfigs.clear();
    virtualHosts.forEach((host, config) -> this.proxySettings.virtualHostConfigs.put(host.toLowerCase(Locale.ENGLISH), config));
  }
}
