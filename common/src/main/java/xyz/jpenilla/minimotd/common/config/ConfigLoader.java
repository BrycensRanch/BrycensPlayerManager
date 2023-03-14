
package xyz.jpenilla.minimotd.common.config;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@DefaultQualifier(NonNull.class)
public final class ConfigLoader<C> {
  private static final TypeSerializerCollection SERIALIZERS;

  static {
    SERIALIZERS = TypeSerializerCollection.builder()
      .register(PlayerCountModifier.serializer())
      .build();
  }

  private final HoconConfigurationLoader loader;
  private final ObjectMapper<C> mapper;
  private final List<Consumer<C>> postProcess;

  public ConfigLoader(
    final Class<C> configClass,
    final Path configPath,
    final UnaryOperator<ConfigurationOptions> optionsModifier
  ) {
    this.postProcess = this.findPostProcessors(configClass);
    this.loader = HoconConfigurationLoader.builder()
      .path(configPath)
      .defaultOptions(options ->
        optionsModifier.apply(options)
          .serializers(builder -> builder.registerAll(SERIALIZERS)))
      .build();
    try {
      this.mapper = ObjectMapper.factory().get(configClass);
    } catch (final SerializationException ex) {
      throw new IllegalStateException(
        "Failed to initialize an object mapper for type: " + configClass.getSimpleName(),
        ex
      );
    }
  }

  private List<Consumer<C>> findPostProcessors(final Class<C> configClass) {
    final List<Consumer<C>> ret = new ArrayList<>();
    for (final Method method : configClass.getDeclaredMethods()) {
      if (method.getAnnotation(PostProcessor.class) == null) {
        continue;
      }
      ret.add(config -> {
        try {
          method.setAccessible(true);
          method.invoke(config);
        } catch (final ReflectiveOperationException ex) {
          throw new RuntimeException("Failed to invoke post processor method", ex);
        }
      });
    }
    return ret;
  }

  public ConfigLoader(
    final @NonNull Class<C> configClass,
    final @NonNull Path configPath
  ) {
    this(configClass, configPath, options -> options);
  }

  public @NonNull C load() throws ConfigurateException {
    final CommentedConfigurationNode node = this.loader.load();
    final C config = this.mapper.load(node);
    for (final Consumer<C> processor : this.postProcess) {
      processor.accept(config);
    }
    return config;
  }

  public void save(final @NonNull C config) throws ConfigurateException {
    final CommentedConfigurationNode node = this.loader.createNode();
    this.mapper.save(config, node);
    this.loader.save(node);
  }
}
