package com.igrium.scaffold.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.igrium.scaffold.compile.ScaffoldCompiler.CompileResult;
import com.igrium.scaffold.compile.ScaffoldCompiler.CompileStatus;
import com.igrium.scaffold.core.Project;
import com.igrium.scaffold.level.Level;
import com.igrium.scaffold.level.element.DemoElement;
import com.igrium.scaffold.level.element.ElementTypes;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.logging.LogUtils;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.Text;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class TestProjectCommand {
    private static DynamicCommandExceptionType ioError = new DynamicCommandExceptionType(
            e -> Text.of("An IO exception occured: " + ((Throwable) e).getMessage()));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess commandRegistryAccess, RegistrationEnvironment env) {
        dispatcher.register(literal("test_project").then(
            argument("name", StringArgumentType.word()).executes(TestProjectCommand::execute)
        ));
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        File runDirectory = context.getSource().getServer().getRunDirectory();
        try {
            Path projectFolder = runDirectory.toPath().resolve(StringArgumentType.getString(context, "name"));

            Project project;
            try {
                project = new Project(projectFolder);
                Files.createDirectories(projectFolder);
                project.getProjectSettings().getSearchPaths().add(Paths.get("blah"));
                project.getProjectSettings().getSearchPaths().add(Paths.get("/absolute"));
                project.saveProjectSettings();

            } catch (IOException e) {
                LogUtils.getLogger().error("An IO exception occurred", e);
                throw ioError.create(e);
            }

            Path compileDir = projectFolder.resolve("compiled");
            Level level = new Level(project);
            DemoElement element = level.createElement(ElementTypes.DEMO_ELEMENT);
            level.getLevelStack().addElement(element);

            CompileResult result = level.compile(compileDir);
            if (result.status() == CompileStatus.COMPLETE) {
                context.getSource().sendFeedback(() -> Text.literal("Compiled to " + compileDir), false);
                return 1;
            } else {
                context.getSource().sendError(Text.literal("Compile failed."));
                LogUtils.getLogger().error("Compile failed.", result.exception());
                return 0;
            }
        } catch (Throwable e) {
            LogUtils.getLogger().error("Error executing command", e);
            return 1;
        }

    }

}
