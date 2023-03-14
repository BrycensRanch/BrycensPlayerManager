
package xyz.jpenilla.minimotd.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Constants {
  private Constants() {
  }

  public static final class PluginMetadata {
    private PluginMetadata() {
    }

    public static final String NAME = "${PLUGIN_NAME}";
    public static final String VERSION = "${PLUGIN_VERSION}";
    public static final String WEBSITE = "${PLUGIN_WEBSITE}";
    public static final String GITHUB_USER = "${GITHUB_USER}";
    public static final String GITHUB_REPO = "${GITHUB_REPO}";
    public static final String GITHUB_REPO_URL = "https://github.com/" + GITHUB_USER + "/" + GITHUB_REPO;
  }

  public static final int MINECRAFT_1_16_PROTOCOL_VERSION = 735;

  public static final Component COMMAND_PREFIX = MiniMessage.miniMessage().deserialize("<white>[</white><gradient:#0047AB:#ADD8E6>" + PluginMetadata.NAME + "</gradient><white>]</white>");
}
