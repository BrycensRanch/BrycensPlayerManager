
package me.brycensranch.BrycensPlayerManager.common.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ComponentColorDownsampler {
  static @NonNull ComponentColorDownsampler downsampler() {
    return ComponentColorDownsamplerImpl.INSTANCE;
  }

  @NonNull Component downsample(@NonNull Component component);

  final class ComponentColorDownsamplerImpl implements ComponentColorDownsampler {
    private static final ComponentColorDownsampler INSTANCE = new ComponentColorDownsamplerImpl();

    private ComponentColorDownsamplerImpl() {
    }

    @Override
    public @NonNull Component downsample(final @NonNull Component component) {
      return GsonComponentSerializer.gson().deserializeFromTree(
        GsonComponentSerializer.colorDownsamplingGson().serializeToTree(component)
      );
    }
  }
}
