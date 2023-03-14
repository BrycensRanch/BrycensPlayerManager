
package xyz.jpenilla.minimotd.common;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

public interface BPMPlatform <I> {
  @NonNull Path dataDirectory();

  @NonNull Logger logger();

  @NonNull I loadIcon(@NonNull BufferedImage image) throws Exception;

  default void onReload() {
  }
}
