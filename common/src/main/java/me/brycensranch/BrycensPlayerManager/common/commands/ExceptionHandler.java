package me.brycensranch.BrycensPlayerManager.common.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.exceptions.*;
import me.brycensranch.BrycensPlayerManager.common.exception.CommandCompleted;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.io.StringWriter;

import static net.kyori.adventure.text.Component.text;

public class ExceptionHandler {
    private ExceptionHandler() {
    }
    public void registerExceptionHandlers(final CommandManager<Audience> manager) {
        manager.registerExceptionHandler(CommandExecutionException.class, this::commandExecution);
        manager.registerExceptionHandler(NoPermissionException.class, this::noPermission);
        manager.registerExceptionHandler(ArgumentParseException.class, this::argumentParsing);
        manager.registerExceptionHandler(InvalidCommandSenderException.class, this::invalidSender);
        manager.registerExceptionHandler(InvalidSyntaxException.class, this::invalidSyntax);
    }
    private void commandExecution(final Audience audience, final CommandExecutionException exception) {
        final Throwable cause = exception.getCause();

        if (cause instanceof CommandCompleted completed) {
            final @Nullable Component message = completed.componentMessage();
            if (message != null) {
                audience.sendMessage(message);
            }
            return;
        }

        Logging.logger().warn("An unexpected error occurred during command execution", cause);

        final TextComponent.Builder message = text();
        message.append(text("COMMAND_EXCEPTION_COMMAND_EXECUTION"));
        decorateWithHoverStacktrace(message, cause);
        decorateAndSend(audience, message);
    }

    private void noPermission(final Audience audience, final NoPermissionException exception) {
        decorateAndSend(audience, Messages.COMMAND_EXCEPTION_NO_PERMISSION);
    }

    private void argumentParsing(final Audience audience, final ArgumentParseException exception) {
        final Throwable cause = exception.getCause();
        final Supplier<Component> fallback = () -> Objects.requireNonNull(ComponentMessageThrowable.getOrConvertMessage(cause));
        final Component message;
        if (cause instanceof final ParserException parserException) {
            final TagResolver[] placeholders = Arrays.stream(parserException.captionVariables())
                    .map(variable -> placeholder(NamingSchemes.SNAKE_CASE.coerce(variable.getKey()), variable.getValue()))
                    .toArray(TagResolver[]::new);
            final String key = Messages.PARSER_EXCEPTION_MESSAGE_PREFIX + parserException.errorCaption().getKey().replace("argument.parse.failure.", "");
            @Nullable Component fromConfig;
            try {
                fromConfig = Messages.componentMessage(key).withPlaceholders(placeholders);
            } catch (final Exception ex) {
                Logging.logger().warn("Could not get message with key '{}'", key, ex);
                fromConfig = null;
            }
            message = fromConfig != null ? fromConfig : fallback.get();
        } else {
            message = fallback.get();
        }
        decorateAndSend(
                audience,
                Messages.COMMAND_EXCEPTION_INVALID_ARGUMENT.withPlaceholders(placeholder("message", message))
        );
    }

    private void invalidSender(final Audience audience, final InvalidCommandSenderException exception) {
        final Component message = Messages.COMMAND_EXCEPTION_INVALID_SENDER_TYPE.withPlaceholders(
                placeholder("required_sender_type", text(exception.getRequiredSender().getSimpleName()))
        );
        decorateAndSend(audience, message);
    }

    private void invalidSyntax(final Audience audience, final InvalidSyntaxException exception) {
        final Component message = Messages.COMMAND_EXCEPTION_INVALID_SYNTAX.withPlaceholders(
                placeholder("correct_syntax", highlightSpecialCharacters(text("/%s".formatted(exception.getCorrectSyntax())), WHITE))
        );
        decorateAndSend(audience, message);
    }

    private static void decorateAndSend(final Audience audience, final ComponentLike componentLike) {
        final Component message = textOfChildren(
                Messages.COMMAND_PREFIX.asComponent()
                        .hoverEvent(Messages.CLICK_FOR_HELP.asComponent())
                        .clickEvent(runCommand("/%s help".formatted(Config.MAIN_COMMAND_LABEL))),
                componentLike
        );
        audience.sendMessage(message);
    }

    private static void decorateWithHoverStacktrace(final TextComponent.Builder message, final Throwable cause) {
        final StringWriter writer = new StringWriter();
        cause.printStackTrace(new PrintWriter(writer));
        final String stackTrace = writer.toString().replaceAll("\t", "    ");
        final TextComponent.Builder hoverText = text();
        final @Nullable Component throwableMessage = ComponentMessageThrowable.getOrConvertMessage(cause);
        if (throwableMessage != null) {
            hoverText.append(throwableMessage)
                    .append(newline())
                    .append(newline());
        }
        hoverText.append(text(stackTrace))
                .append(newline())
                .append(text("    "))
                .append(Messages.CLICK_TO_COPY.asComponent().color(GRAY).decorate(ITALIC));

        message.hoverEvent(hoverText.build());
        message.clickEvent(copyToClipboard(stackTrace));
    }
}
