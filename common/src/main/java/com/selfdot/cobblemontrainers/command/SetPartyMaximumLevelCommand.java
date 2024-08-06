package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.libs.minecraft.MinecraftMod;
import com.selfdot.libs.minecraft.permissions.Permission;
import com.selfdot.libs.minecraft.permissions.PermissionLevel;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class SetPartyMaximumLevelCommand extends TrainerCommand {

    public SetPartyMaximumLevelCommand(MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher) {
        super(mod, dispatcher, "setpartymaximumlevel", new Permission(
            "trainers.setpartymaximumlevel", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
        ));
    }

    @Override
    protected LiteralArgumentBuilder<ServerCommandSource> node(LiteralArgumentBuilder<ServerCommandSource> root) {
        return super.node(root
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("trainer", string())
                .suggests(new TrainerNameSuggestionProvider())
                .then(RequiredArgumentBuilder.<ServerCommandSource, Integer>
                    argument("partyMaximumLevel", integer(1, 100))
                    .executes(this)
                )
            )
        );
    }

    @Override
    protected int execute(CommandContext<ServerCommandSource> context, Trainer trainer) throws CommandSyntaxException {
        int partyMaximumLevel = IntegerArgumentType.getInteger(context, "partyMaximumLevel");
        trainer.setPartyMaximumLevel(partyMaximumLevel);
        context.getSource().sendMessage(Text.literal(
                "Set party maximum level for trainer " + trainer.getName() + " to " + partyMaximumLevel
        ));

        return SINGLE_SUCCESS;
    }
}