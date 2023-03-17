package me.brycensranch.BrycensPlayerManager.common.commands;

import cloud.commandframework.brigadier.BrigadierManagerHolder;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BrigadierSetup {
    private BrigadierSetup() {
    }

    public static void setup(final BrigadierManagerHolder<Audience> manager) {
        final @Nullable CloudBrigadierManager<Audience, ?> brigManager = manager.brigadierManager();
        Objects.requireNonNull(brigManager);

        brigManager.setNativeNumberSuggestions(false);

    }
}
