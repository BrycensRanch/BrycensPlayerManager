package me.brycensranch.BrycensPlayerManager.common.exception;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.util.ComponentMessageThrowable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;

@DefaultQualifier(NonNull.class)
public final class CommandCompleted extends RuntimeException implements ComponentMessageThrowable {
    @Serial
    private static final long serialVersionUID = -8318440562349647391L;

    private final @Nullable Component message;

    private CommandCompleted(final @Nullable Component message) {
        this.message = message;
    }

    public static CommandCompleted withoutMessage() {
        return new CommandCompleted(null);
    }

    public static CommandCompleted withMessage(final ComponentLike message) {
        return new CommandCompleted(message.asComponent());
    }

    @Override
    public @Nullable Component componentMessage() {
        return this.message;
    }

    @Override
    public String getMessage() {
        return PlainTextComponentSerializer.plainText().serializeOr(this.message, "No message.");
    }
}