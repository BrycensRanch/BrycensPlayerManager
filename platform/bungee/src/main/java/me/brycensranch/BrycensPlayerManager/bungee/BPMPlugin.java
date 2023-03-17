
package me.brycensranch.BrycensPlayerManager.bungee;

import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.file.Path;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.brycensranch.BrycensPlayerManager.common.BrycensPlayerManager;
import me.brycensranch.BrycensPlayerManager.common.BPMPlatform;
import me.brycensranch.BrycensPlayerManager.common.util.UpdateChecker;

public final class BPMPlugin extends Plugin implements BPMPlatform<Favicon> {
  private Logger logger;
  private BungeeAudiences audiences;
  private BrycensPlayerManager<Favicon> brycensPlayerManager;

  public @NonNull BrycensPlayerManager<Favicon> brycensPlayerManager() {
    return this.brycensPlayerManager;
  }

  @Override
  public void onEnable() {
    this.logger = LoggerFactory.getLogger(this.getDescription().getName());
    this.brycensPlayerManager = new BrycensPlayerManager<>(this);
    this.brycensPlayerManager.configManager().loadExtraConfigs();
    this.audiences = BungeeAudiences.create(this);
    this.injectTravertineGson();
    this.getProxy().getPluginManager().registerListener(this, new PingListener(this.brycensPlayerManager));
    this.getProxy().getPluginManager().registerCommand(this, new BungeeCommand(this));
    final Metrics metrics = new Metrics(this, 8137);

    if (this.brycensPlayerManager.configManager().pluginSettings().updateChecker()) {
      this.getProxy().getScheduler().runAsync(this, () ->
        new UpdateChecker().checkVersion().forEach(this.logger::info));
    }
  }

  @Override
  public @NonNull Path dataDirectory() {
    return this.getDataFolder().toPath();
  }

  @Override
  public @NonNull Logger logger() {
    return this.logger;
  }

  @Override
  public @NonNull Favicon loadIcon(final @NonNull BufferedImage image) {
    return Favicon.create(image);
  }

  public @NonNull BungeeAudiences audiences() {
    return this.audiences;
  }

  @Override
  public void onReload() {
    this.brycensPlayerManager.configManager().loadExtraConfigs();
  }

  private void injectTravertineGson() {
    final Field gsonLegacyField = findDeclaredField(ProxyServer.getInstance().getClass(), "gsonLegacy");
    if (gsonLegacyField != null) {
      try {
        BungeeComponentSerializer.inject((Gson) gsonLegacyField.get(ProxyServer.getInstance()));
      } catch (final IllegalAccessException ex) {
        this.brycensPlayerManager.logger().warn("Failed to inject into Travertine's gsonLegacy gson instance. There will likely be issues with 1.7.x clients.", ex);
      }
    }
  }

  private static @Nullable Field findDeclaredField(final @NonNull Class<?> holder, final @NonNull String name) {
    try {
      return holder.getDeclaredField(name);
    } catch (final NoSuchFieldException ex) {
      return null;
    }
  }
}
