package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.libs.minecraft.MinecraftMod;
import com.selfdot.libs.minecraft.permissions.Permission;
import com.selfdot.libs.minecraft.permissions.PermissionLevel;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class SetCanOnlyBeatOnceCommand extends TrainerCommand {

    public SetCanOnlyBeatOnceCommand(MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher) {
        super(mod, dispatcher, "setcanonlybeatonce", new Permission(
            "trainers.setcanonlybeatonce", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
        ));
    }

    @Override
    protected LiteralArgumentBuilder<ServerCommandSource> node(LiteralArgumentBuilder<ServerCommandSource> root) {
        return super.node(root
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("trainer", string())
                .suggests(new TrainerNameSuggestionProvider())
                .then(RequiredArgumentBuilder.<ServerCommandSource, Boolean>
                    argument("canOnlyBeatOnce", bool())
                    .executes(this)
                )
            )
        );
    }

    @Override
    protected int execute(CommandContext<ServerCommandSource> context, Trainer trainer) throws CommandSyntaxException {
        boolean canOnlyBeatOnce = BoolArgumentType.getBool(context, "canOnlyBeatOnce");
        trainer.setCanOnlyBeatOnce(canOnlyBeatOnce);
        context.getSource().sendMessage(Text.literal(
            "Set trainer " + trainer.getName() + " can only beat once to " + canOnlyBeatOnce
        ));
        return SINGLE_SUCCESS;
    }

}
