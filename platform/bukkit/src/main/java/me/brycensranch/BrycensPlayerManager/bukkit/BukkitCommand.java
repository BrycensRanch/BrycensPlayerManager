
package me.brycensranch.BrycensPlayerManager.bukkit;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.checkerframework.checker.nullness.qual.NonNull;
import me.brycensranch.BrycensPlayerManager.common.CommandHandler;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

final class BukkitCommand implements CommandExecutor, TabCompleter {
  private final BPMPlugin plugin;
  private final CommandHandler handler;

  BukkitCommand(final @NonNull BPMPlugin plugin) {
    this.plugin = plugin;
    this.handler = new CommandHandler(plugin.brycensPlayerManager());
  }

  @Override
  public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String label, final @NonNull String[] args) {
    final Audience audience = this.plugin.audiences().sender(sender);
    if (!sender.hasPermission("minimotd.admin")) {
      audience.sendMessage(text("No permission.", RED));
      return true;
    }

    if (args.length == 0) {
      this.onInvalidUse(audience);
      return true;
    }

    switch (args[0]) {
      case "about":
        this.handler.about(audience);
        return true;
      case "help":
        this.handler.help(audience);
        return true;
      case "reload":
        this.handler.reload(audience);
        return true;
    }

    this.onInvalidUse(audience);
    return true;
  }

  private void onInvalidUse(final @NonNull Audience audience) {
    audience.sendMessage(text("Invalid command usage. Use '/minimotd help' for a list of command provided by BrycensPlayerManager.", RED)
      .hoverEvent(text("Click to execute '/minimotd help'"))
      .clickEvent(runCommand("/minimotd help")));
  }

  private static final List<String> COMMANDS = ImmutableList.of("about", "reload", "help");

  @Override
  public List<String> onTabComplete(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String alias, final @NonNull String[] args) {
    if (args.length < 2 && sender.hasPermission("minimotd.admin")) {
      return COMMANDS;
    }
    return Collections.emptyList();
  }
}
