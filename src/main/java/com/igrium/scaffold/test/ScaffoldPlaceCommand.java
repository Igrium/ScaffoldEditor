package com.igrium.scaffold.test;

import com.igrium.scaffold.engine.IServerWorldMixin;
import com.igrium.scaffold.world.ScaffoldWorld;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.*;

public class ScaffoldPlaceCommand {

    private static final SimpleCommandExceptionType NO_SCAFFOLD_EXCEPTION = new SimpleCommandExceptionType(Text.literal("This is not a Scaffold world."));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, RegistrationEnvironment env) {
        dispatcher.register(literal("setblock_scaffold").then(
            argument("pos", BlockPosArgumentType.blockPos()).then(
                argument("block", BlockStateArgumentType.blockState(commandRegistryAccess)).executes(ScaffoldPlaceCommand::execute)
            )
        ));
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var worldOpt = ((IServerWorldMixin) context.getSource().getWorld()).getScaffoldWorld();
        if (worldOpt.isEmpty()) {
            throw NO_SCAFFOLD_EXCEPTION.create();
        }
        ScaffoldWorld world = worldOpt.get();

        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        BlockState block = BlockStateArgumentType.getBlockState(context, "block").getBlockState();

        world.setBlock(pos.getX(), pos.getY(), pos.getZ(), block);
        context.getSource().sendFeedback(() -> Text.literal("Placed " + block + " at " + pos), true);

        return 1;
    }
}
