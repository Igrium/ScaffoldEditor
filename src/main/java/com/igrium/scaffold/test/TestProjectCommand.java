package com.igrium.scaffold.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.igrium.scaffold.core.Project;
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
        Path projectFolder = runDirectory.toPath().resolve(StringArgumentType.getString(context, "name"));

        try {
            Project project = new Project(projectFolder);
            project.getProjectSettings().getSearchPaths().add(Paths.get("blah"));
            project.getProjectSettings().getSearchPaths().add(Paths.get("/absolute"));
            project.saveProjectSettings();

            for (Path path : project.getSearchPaths()) {
                context.getSource().sendFeedback(() -> Text.of(path.toString()), false);
            }
        } catch (IOException e) {
            LogUtils.getLogger().error("An IO exception occurred", e);
            throw ioError.create(e);
        }
        return 1;
    }

}