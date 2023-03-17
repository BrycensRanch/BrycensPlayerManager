
package me.brycensranch.BrycensPlayerManager.fabric;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.commands.CommandSourceStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.brycensranch.BrycensPlayerManager.common.CommandHandler;
import me.brycensranch.BrycensPlayerManager.common.BrycensPlayerManager;
import me.brycensranch.BrycensPlayerManager.common.BPMPlatform;
import me.brycensranch.BrycensPlayerManager.common.util.UpdateChecker;

import static net.minecraft.commands.Commands.literal;

public final class BPMFabric implements ModInitializer, BPMPlatform<String> {
  private static BPMFabric instance = null;

  private final Logger logger = LoggerFactory.getLogger(BrycensPlayerManager.class);
  private final Path dataDirectory = FabricLoader.getInstance().getConfigDir().resolve("BrycensPlayerManager");
  private final BrycensPlayerManager<String> brycensPlayerManager = new BrycensPlayerManager<>(this);

  private FabricServerAudiences audiences;

  public BPMFabric() {
    if (instance != null) {
      throw new IllegalStateException("Cannot create a second instance of " + this.getClass().getName());
    }
    instance = this;
  }

  public @NonNull BrycensPlayerManager<String> brycensPlayerManager() {
    return this.brycensPlayerManager;
  }

  @Override
  public void onInitialize() {
    this.registerCommand();
    ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
      this.audiences = FabricServerAudiences.of(minecraftServer);
      if (this.brycensPlayerManager.configManager().pluginSettings().updateChecker()) {
        CompletableFuture.runAsync(() -> new UpdateChecker().checkVersion().forEach(this.logger()::info));
      }
    });
    this.brycensPlayerManager.logger().info("Done initializing BrycensPlayerManager");
  }

  private void registerCommand() {
    final class WrappingExecutor implements Command<CommandSourceStack> {
      private final CommandHandler.Executor handler;

      WrappingExecutor(final CommandHandler.@NonNull Executor handler) {
        this.handler = handler;
      }

      @Override
      public int run(final @NonNull CommandContext<CommandSourceStack> context) {
        this.handler.execute(BPMFabric.this.audiences.audience(context.getSource()));
        return Command.SINGLE_SUCCESS;
      }
    }

    final CommandHandler handler = new CommandHandler(this.brycensPlayerManager);
    CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, commandSelection) -> dispatcher.register(
      literal("minimotd")
        .requires(source -> source.hasPermission(4))
        .then(literal("reload").executes(new WrappingExecutor(handler::reload)))
        .then(literal("about").executes(new WrappingExecutor(handler::about)))
        .then(literal("help").executes(new WrappingExecutor(handler::help)))
    ));
  }

  public @NonNull FabricServerAudiences audiences() {
    return this.audiences;
  }

  public static @NonNull BPMFabric get() {
    return instance;
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
  public @NonNull String loadIcon(final @NonNull BufferedImage bufferedImage) throws Exception {
    final ByteBuf byteBuf = Unpooled.buffer();
    final String icon;
    try {
      ImageIO.write(bufferedImage, "PNG", new ByteBufOutputStream(byteBuf));
      final ByteBuffer base64 = Base64.getEncoder().encode(byteBuf.nioBuffer());
      icon = "data:image/png;base64," + StandardCharsets.UTF_8.decode(base64);
    } finally {
      byteBuf.release();
    }
    return icon;
  }
}
