package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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

import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class SetGroupCommand extends TrainerCommand {

    public SetGroupCommand(MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher) {
        super(mod, dispatcher, "setgroup", new Permission(
            "trainers.setgroup", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
        ));
    }

    @Override
    protected LiteralArgumentBuilder<ServerCommandSource> node(LiteralArgumentBuilder<ServerCommandSource> root) {
        return super.node(root
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("trainer", string())
                .suggests(new TrainerNameSuggestionProvider())
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("group", string())
                    .suggests(new TrainerGroupSuggestionProvider())
                    .executes(this)
                )
            )
        );
    }

    @Override
    protected int execute(CommandContext<ServerCommandSource> context, Trainer trainer) throws CommandSyntaxException {
        String group = StringArgumentType.getString(context, "group");
        trainer.setGroup(group);
        context.getSource().sendMessage(Text.literal(
            "Set group for trainer " + trainer.getName() + " to " + group
        ));
        return SINGLE_SUCCESS;
    }

}
