
package xyz.jpenilla.minimotd.bungee;

import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.CommandHandler;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

final class BungeeCommand extends Command {
  private final BrycensPlayerManagerPlugin plugin;
  private final CommandHandler handler;

  BungeeCommand(final @NonNull BrycensPlayerManagerPlugin plugin) {
    super("minimotd");
    this.plugin = plugin;
    this.handler = new CommandHandler(plugin.brycensPlayerManager());
  }

  @Override
  public void execute(final @NonNull CommandSender sender, final @NonNull String @NonNull [] args) {
    final Audience audience = this.plugin.audiences().sender(sender);
    if (!sender.hasPermission("minimotd.admin")) {
      audience.sendMessage(text("No permission.", RED));
      return;
    }

    if (args.length == 0) {
      this.onInvalidUse(audience);
      return;
    }

    switch (args[0]) {
      case "about":
        this.handler.about(audience);
        return;
      case "help":
        this.handler.help(audience);
        return;
      case "reload":
        this.handler.reload(audience);
        return;
    }

    this.onInvalidUse(audience);
  }

  private void onInvalidUse(final @NonNull Audience audience) {
    audience.sendMessage(text("Invalid command usage. Use '/minimotd help' for a list of command provided by BrycensPlayerManager.", RED)
      .hoverEvent(text("Click to execute '/minimotd help'"))
      .clickEvent(runCommand("/minimotd help")));
  }
}
