
package xyz.jpenilla.minimotd.velocity;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.Favicon;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Set;
import org.bstats.velocity.Metrics;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.BrycensPlayerManager;
import xyz.jpenilla.minimotd.common.BPMPlatform;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;

@Plugin(
  id = "${project.name}",
  name = Constants.PluginMetadata.NAME,
  version = Constants.PluginMetadata.VERSION,
  description = "${description}",
  url = "${url}",
  authors = {"romvnly"}
)
public final class BrycensPlayerManagerPlugin implements BPMPlatform<Favicon> {
  private static final Set<Class<?>> LISTENER_CLASSES = ImmutableSet.of(
    PingListener.class
  );

  private final BrycensPlayerManager<Favicon> brycensPlayerManager;
  private final ProxyServer server;
  private final Logger logger;
  private final CommandManager commandManager;
  private final Path dataDirectory;
  private final Metrics.Factory metricsFactory;
  private final Injector injector;

  @Inject
  public BrycensPlayerManagerPlugin(
    final @NonNull ProxyServer server,
    final @NonNull Logger logger,
    final @NonNull CommandManager commandManager,
    @DataDirectory final @NonNull Path dataDirectory,
    final Metrics.@NonNull Factory metricsFactory,
    final @NonNull Injector injector
  ) {
    this.server = server;
    this.logger = logger;
    this.commandManager = commandManager;
    this.dataDirectory = dataDirectory;
    this.metricsFactory = metricsFactory;
    this.brycensPlayerManager = new BrycensPlayerManager<>(this);
    this.brycensPlayerManager.configManager().loadExtraConfigs();
    this.injector = injector.createChildInjector(new AbstractModule() {
      @Override
      protected void configure() {
        this.bind(new TypeLiteral<BrycensPlayerManager<Favicon>>() {
        }).toInstance(BrycensPlayerManagerPlugin.this.brycensPlayerManager);
      }
    });
  }

  @Subscribe
  public void onProxyInitialization(final @NonNull ProxyInitializeEvent event) {
    for (final Class<?> clazz : LISTENER_CLASSES) {
      this.server.getEventManager().register(this, this.injector.getInstance(clazz));
    }
    this.registerCommand();
    this.metricsFactory.make(this, 10257);
    if (this.brycensPlayerManager.configManager().pluginSettings().updateChecker()) {
      this.server.getScheduler().buildTask(
        this,
        () -> new UpdateChecker().checkVersion().forEach(this.logger::info)
      ).schedule();
    }
  }

  private void registerCommand() {
    final class WrappingExecutor implements Command<CommandSource> {
      private final CommandHandler.Executor handler;

      WrappingExecutor(final CommandHandler.@NonNull Executor handler) {
        this.handler = handler;
      }

      @Override
      public int run(final @NonNull CommandContext<CommandSource> context) {
        this.handler.execute(context.getSource());
        return Command.SINGLE_SUCCESS;
      }
    }

    final CommandHandler handler = new CommandHandler(this.brycensPlayerManager);
    this.commandManager.register(this.commandManager.metaBuilder("minimotd").build(), new BrigadierCommand(
      LiteralArgumentBuilder.<CommandSource>literal("minimotd")
        .requires(source -> source.hasPermission("minimotd.admin"))
        .then(LiteralArgumentBuilder.<CommandSource>literal("help").executes(new WrappingExecutor(handler::help)))
        .then(LiteralArgumentBuilder.<CommandSource>literal("about").executes(new WrappingExecutor(handler::about)))
        .then(LiteralArgumentBuilder.<CommandSource>literal("reload").executes(new WrappingExecutor(handler::reload)))
    ));
  }

  @Override
  public @NonNull Path dataDirectory() {
    return this.dataDirectory;
  }

  @Override
  public @NonNull Logger logger() {
    return this.logger;
  }

  @Override
  public @NonNull Favicon loadIcon(final @NonNull BufferedImage image) {
    return Favicon.create(image);
  }

  @Override
  public void onReload() {
    this.brycensPlayerManager.configManager().loadExtraConfigs();
  }
}
