
package me.brycensranch.BrycensPlayerManager.bukkit;

import io.papermc.lib.PaperLib;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.brycensranch.BrycensPlayerManager.common.BrycensPlayerManager;
import me.brycensranch.BrycensPlayerManager.common.BPMPlatform;
import me.brycensranch.BrycensPlayerManager.common.util.UpdateChecker;

public final class BPMPlugin extends JavaPlugin implements BPMPlatform<CachedServerIcon> {
  private static final boolean PAPER_PING_EVENT_EXISTS = findClass("com.destroystokyo.paper.event.server.PaperServerListPingEvent") != null;

  private Logger logger;
  private BrycensPlayerManager<CachedServerIcon> brycensPlayerManager;
  private BukkitAudiences audiences;

  @Override
  public void onEnable() {
    this.logger = LoggerFactory.getLogger(this.getName());
    this.brycensPlayerManager = new BrycensPlayerManager<>(this);
    this.audiences = BukkitAudiences.create(this);

    if (PAPER_PING_EVENT_EXISTS) {
      this.getServer().getPluginManager().registerEvents(new PaperPingListener(this.brycensPlayerManager), this);
    } else {
      this.getServer().getPluginManager().registerEvents(new PingListener(this.brycensPlayerManager), this);
      if (PaperLib.getMinecraftVersion() >= 12) { // PaperServerListPingEvent was added in 1.12
        this.suggestPaper();
      }
    }

    final PluginCommand command = this.getCommand("bpm");
    if (command != null) {
      final BukkitCommand bukkitCommand = new BukkitCommand(this);
      command.setExecutor(bukkitCommand);
      command.setTabCompleter(bukkitCommand);
    }

    if (this.brycensPlayerManager.configManager().pluginSettings().updateChecker()) {
      this.getServer().getScheduler().runTaskAsynchronously(this, () ->
        new UpdateChecker().checkVersion().forEach(this.logger::info));
    }
  }

  public @NonNull BrycensPlayerManager<CachedServerIcon> brycensPlayerManager() {
    return this.brycensPlayerManager;
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
  public @NonNull CachedServerIcon loadIcon(final @NonNull BufferedImage image) throws Exception {
    return this.getServer().loadServerIcon(image);
  }

  public @NonNull BukkitAudiences audiences() {
    return this.audiences;
  }

  private void suggestPaper() {
    this.logger.warn("======================================================");
    this.logger.warn(" BrycensPlayerManager works better if you use Paper as your server");
    this.logger.warn(" software.");
    this.logger.warn(" ");
    this.logger.warn(" Spigot does not include the necessary APIs for all");
    this.logger.warn(" of BrycensPlayerManager's features to operate. BrycensPlayerManager was");
    this.logger.warn(" designed to work with Paper and it's expanded API");
    this.logger.warn(" for full compatibility.");
    this.logger.warn(" ");
    this.logger.warn(" Get Paper from https://papermc.io/downloads");
    this.logger.warn("======================================================");
  }

  private static @Nullable Class<?> findClass(final @NonNull String className) {
    try {
      return Class.forName(className);
    } catch (final ClassNotFoundException ex) {
      return null;
    }
  }
}
