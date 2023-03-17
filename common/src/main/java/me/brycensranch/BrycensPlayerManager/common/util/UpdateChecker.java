
package me.brycensranch.BrycensPlayerManager.common.util;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.brycensranch.BrycensPlayerManager.common.Constants;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class UpdateChecker {
  private final JsonParser parser = new JsonParser();

  public @NonNull List<String> checkVersion() {
    final JsonArray result;
    final String url = String.format("https://api.github.com/repos/%s/%s/releases", Constants.PluginMetadata.GITHUB_USER, Constants.PluginMetadata.GITHUB_REPO);
    try (final InputStream is = new URL(url).openStream(); InputStreamReader reader = new InputStreamReader(is, Charsets.UTF_8)) {
      result = this.parser.parse(reader).getAsJsonArray();
    } catch (final IOException ex) {
      return Collections.singletonList("Cannot look for updates: " + ex.getMessage());
    }
    final Map<String, String> versionMap = new LinkedHashMap<>();
    result.forEach(element -> versionMap.put(element.getAsJsonObject().get("tag_name").getAsString(), element.getAsJsonObject().get("html_url").getAsString()));
    final List<String> versionList = new LinkedList<>(versionMap.keySet());
    final String currentVersion = "v" + Constants.PluginMetadata.VERSION;
    if (versionList.get(0).equals(currentVersion)) {
      return Collections.emptyList(); // Up to date, do nothing
    }
    if (currentVersion.contains("SNAPSHOT")) {
      return ImmutableList.of(
        "This server is running a development build of " + Constants.PluginMetadata.NAME + "! (" + currentVersion + ")",
        "The latest official release is " + versionList.get(0)
      );
    }
    final int versionsBehind = versionList.indexOf(currentVersion);
    return ImmutableList.of(
      "There is an update available for " + Constants.PluginMetadata.NAME + "!",
      "This server is running version " + currentVersion + ", which is " + (versionsBehind == -1 ? "UNKNOWN" : versionsBehind) + " versions outdated.",
      "Download the latest version, " + versionList.get(0) + " from GitHub at the link below:",
      versionMap.get(versionList.get(0))
    );
  }
}
