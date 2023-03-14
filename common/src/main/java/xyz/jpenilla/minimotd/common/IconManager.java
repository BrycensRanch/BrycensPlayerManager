
package xyz.jpenilla.minimotd.common;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class IconManager<I> {
  private final Map<String, I> icons = new ConcurrentHashMap<>();
  private final BrycensPlayerManager<I> brycensPlayerManager;
  private final Path iconsDirectory;

  public IconManager(final @NonNull BrycensPlayerManager<I> brycensPlayerManager) {
    this.brycensPlayerManager = brycensPlayerManager;
    this.iconsDirectory = brycensPlayerManager.dataDirectory().resolve("icons");
    this.loadIcons();
  }

  public void loadIcons() {
    this.icons.clear();
    try {
      if (!Files.exists(this.iconsDirectory)) {
        Files.createDirectories(this.iconsDirectory);
      }
      try (final Stream<Path> stream = Files.list(this.iconsDirectory)) {
        stream.filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().endsWith(".png"))
          .forEach(this::loadIcon);
      }
    } catch (final IOException ex) {
      throw new RuntimeException("Exception loading server icons", ex);
    }
  }

  private void loadIcon(final @NonNull Path iconFile) {
    try (final InputStream inputStream = Files.newInputStream(iconFile)) {
      final BufferedImage bufferedImage = ImageIO.read(inputStream);
      if (bufferedImage.getHeight() == 64 && bufferedImage.getWidth() == 64) {
        final I newIcon = this.brycensPlayerManager.platform().loadIcon(bufferedImage);
        this.icons.put(iconFile.getFileName().toString().split("\\.")[0], newIcon);
      } else {
        this.brycensPlayerManager.logger().warn("Could not load {}: image must be 64x64px", iconFile.getFileName());
      }
    } catch (final Exception ex) {
      this.brycensPlayerManager.logger().warn("Could not load {}: invalid image file", iconFile.getFileName(), ex);
    }
  }

  public @Nullable I icon(final @Nullable String iconString) {
    if (this.icons.isEmpty()) {
      return null;
    }

    if (iconString == null || "random".equals(iconString)) {
      final int randomIndex = ThreadLocalRandom.current().nextInt(this.icons.size());
      final Iterator<I> iterator = this.icons.values().iterator();
      for (int i = 0; i < randomIndex; i++) {
        iterator.next();
      }
      return iterator.next();
    }

    return this.icons.get(iconString);
  }
}
