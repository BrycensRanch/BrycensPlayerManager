
package xyz.jpenilla.minimotd.sponge8;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;
import org.spongepowered.plugin.metadata.PluginMetadata;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.BrycensPlayerManager;
import xyz.jpenilla.minimotd.common.BPMPlatform;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;

@Plugin("minimotd-sponge8")
public final class BrycensPlayerManagerPlugin implements BPMPlatform<Favicon> {
  private final Path dataDirectory;
  private final PluginMetadata pluginMetadata;
  private final Logger logger;
  private final PluginContainer pluginContainer;
  private final BrycensPlayerManager<Favicon> brycensPlayerManager;
  private final Injector injector;

  @Inject
  public BrycensPlayerManagerPlugin(
    @ConfigDir(sharedRoot = false) final @NonNull Path dataDirectory,
    final @NonNull PluginContainer pluginContainer,
    final @NonNull Injector injector
  ) {
    this.dataDirectory = dataDirectory;
    this.pluginContainer = pluginContainer;
    this.pluginMetadata = pluginContainer.metadata();
    this.logger = LoggerFactory.getLogger(this.pluginMetadata.id());
    this.brycensPlayerManager = new BrycensPlayerManager<>(this);
    this.injector = injector.createChildInjector(new AbstractModule() {
      @Override
      protected void configure() {
        this.bind(new TypeLiteral<BrycensPlayerManager<Favicon>>() {
        }).toInstance(BrycensPlayerManagerPlugin.this.brycensPlayerManager);
      }
    });
    Sponge.eventManager().registerListener(
      EventListenerRegistration.builder(ClientPingServerEvent.class)
        .plugin(pluginContainer)
        .listener(this.injector.getInstance(ClientPingServerEventListener.class))
        .order(Order.DEFAULT)
        .build()
    );
  }

  @Listener
  public void onGameLoaded(final @NonNull LoadedGameEvent event) {
    if (this.brycensPlayerManager.configManager().pluginSettings().updateChecker()) {
      Sponge.asyncScheduler().submit(Task.builder()
        .plugin(this.pluginContainer)
        .execute(() -> new UpdateChecker().checkVersion().forEach(this.logger::info))
        .build());
    }
  }

  @Listener
  public void registerCommands(final @NonNull RegisterCommandEvent<Command.Parameterized> event) {
    final class WrappingExecutor implements CommandExecutor {
      private final CommandHandler.Executor handler;

      WrappingExecutor(final CommandHandler.@NonNull Executor handler) {
        this.handler = handler;
      }

      @Override
      public CommandResult execute(final @NonNull CommandContext context) {
        this.handler.execute(context.cause().audience());
        return CommandResult.success();
      }
    }

    final CommandHandler handler = new CommandHandler(this.brycensPlayerManager);
    final Command.Parameterized about = Command.builder()
      .executor(new WrappingExecutor(handler::about))
      .build();
    final Command.Parameterized help = Command.builder()
      .executor(new WrappingExecutor(handler::help))
      .build();
    final Command.Parameterized reload = Command.builder()
      .executor(new WrappingExecutor(handler::reload))
      .build();
    event.register(
      this.pluginContainer,
      Command.builder()
        .permission("minimotd.admin")
        .addChild(about, "about")
        .addChild(help, "help")
        .addChild(reload, "reload")
        .build(),
      "minimotd"
    );
  }

  @Listener
  public void onRefresh(final @NonNull RefreshGameEvent event) {
    try {
      this.brycensPlayerManager.reload();
    } catch (final Exception ex) {
      this.brycensPlayerManager.logger().warn("Failed to reload BrycensPlayerManager.", ex);
    }
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
  public @NonNull Favicon loadIcon(final @NonNull BufferedImage image) throws Exception {
    return Favicon.load(image);
  }
}
