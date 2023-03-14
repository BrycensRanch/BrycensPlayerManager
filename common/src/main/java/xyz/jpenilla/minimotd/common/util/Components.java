
package xyz.jpenilla.minimotd.common.util;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

@DefaultQualifier(NonNull.class)
public final class Components {
  private Components() {
    throw new IllegalStateException();
  }

  public static TextComponent ofChildren(final ComponentLike... children) {
    if (children.length == 0) {
      return empty();
    }

    return text().append(children).build();
  }
}
