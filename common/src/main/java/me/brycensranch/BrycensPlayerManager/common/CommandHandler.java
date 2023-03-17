
package me.brycensranch.BrycensPlayerManager.common;

import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import me.brycensranch.BrycensPlayerManager.common.util.Components;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.LinearComponents.linear;
import static net.kyori.adventure.text.event.ClickEvent.openUrl;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;

@DefaultQualifier(NonNull.class)
public final class CommandHandler {
  private final BrycensPlayerManager<?> brycensPlayerManager;

  public CommandHandler(final BrycensPlayerManager<?> brycensPlayerManager) {
    this.brycensPlayerManager = brycensPlayerManager;
  }

  public void about(final Audience audience) {
    final Component header = miniMessage("<gradient:white:#007FFF:white>                               ").decorate(STRIKETHROUGH);
    Stream.of(
      header,
      text()
        .hoverEvent(miniMessage("<rainbow>click me!"))
        .clickEvent(openUrl(Constants.PluginMetadata.WEBSITE))
        .content(Constants.PluginMetadata.NAME)
        .color(WHITE)
        .append(space())
        .append(miniMessage("<gradient:#0047AB:#007FFF>" + Constants.PluginMetadata.VERSION))
        .build(),
      text()
        .content("By ")
        .color(GRAY)
        .append(text("jmp", WHITE))
        .build(),
      header
    ).forEach(audience::sendMessage);
  }

  public void reload(final Audience audience) {
    try {
      this.brycensPlayerManager.reload();
    } catch (final Exception ex) {
      this.brycensPlayerManager.logger().warn("Failed to reload BrycensPlayerManager. Ensure there are no errors in your config files.", ex);
      audience.sendMessage(Components.ofChildren(
        Constants.COMMAND_PREFIX,
        space(),
        text("Failed to reload BrycensPlayerManager. Ensure there are no errors in your config files. See console for more details.", RED)
      ));
      return;
    }

    audience.sendMessage(Components.ofChildren(
      Constants.COMMAND_PREFIX,
      space(),
      text("Done reloading configuration.", GREEN)
    ));
  }

  public void help(final Audience audience) {
    Stream.of(
      linear(Constants.COMMAND_PREFIX, space(), text(Constants.PluginMetadata.NAME + " command help", WHITE)),
      commandInfo("minimotd about", "Show information about BrycensPlayerManager"),
      commandInfo("minimotd reload", "Reload BrycensPlayerManager configuration files"),
      commandInfo("minimotd help", "Show this help menu")
    ).forEach(audience::sendMessage);
  }

  private static Component commandInfo(final String command, final String description) {
    return text()
      .content(" - ")
      .color(GRAY)
      .append(text('/', WHITE))
      .append(text(command, color(0x007FFF)))
      .append(text(':'))
      .append(space())
      .append(text(description, WHITE))
      .hoverEvent(text()
        .content("Click to execute '")
        .color(GRAY)
        .append(text("/" + command, WHITE))
        .append(text("'"))
        .build())
      .clickEvent(runCommand("/" + command))
      .build();
  }

  private static Component miniMessage(final String message) {
    return MiniMessage.miniMessage().deserialize(message);
  }

  @FunctionalInterface
  public interface Executor {
    void execute(Audience audience);
  }
}
