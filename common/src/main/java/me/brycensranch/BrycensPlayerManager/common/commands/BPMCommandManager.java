package me.brycensranch.BrycensPlayerManager.common.commands;

import cloud.commandframework.CommandManager;

import cloud.commandframework.Command;
import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import me.brycensranch.BrycensPlayerManager.common.BPMPlatform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;


public class BPMCommandManager {
        final static Function<Audience, Audience> mapperFunction = Function.identity();
        //
        // This is a function that will provide a command execution coordinator that parses and executes commands
        // asynchronously
        //
        final static Function<CommandTree<Audience>, CommandExecutionCoordinator<Audience>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<Audience>builder().build();
        public CommandConfirmationManager<Audience> confirmationManager;
        public AnnotationParser<Audience> annotationParser;
        public final CommandManager<Audience> commandManager;
        public final BPMPlatform platform;
        public final ExceptionHandler exceptionHandler;

    public BPMCommandManager(CommandManager<Audience> commandManager, BPMPlatform platform, ExceptionHandler exceptionHandler) {
        this.platform = platform;
        this.commandManager = platform.createCommandManager();
        this.exceptionHandler = exceptionHandler;
    }

        public void registerSubcommand(UnaryOperator<Command.Builder<Audience>> builderModifier) {
            this.command(builderModifier.apply(this.rootBuilder()));
        }

        private Command.@NonNull Builder<Audience> rootBuilder() {
            //
            // Parse all @CommandMethod-annotated methods
            //
            this.annotationParser.parse(this);
            // Parse all @CommandContainer-annotated classes
            try {
                this.annotationParser.parseContainers();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return this.commandBuilder("bpm", "brycensplayermanager");

                    /* MinecraftHelp uses the MinecraftExtrasMetaKeys.DESCRIPTION meta, this is just so we give Bukkit a description
                     * for our commands in the Bukkit and EssentialsX '/help' command */
                    .meta(CommandMeta.DESCRIPTION, "The oldest anarchy server in Minecraft");
        }

}

