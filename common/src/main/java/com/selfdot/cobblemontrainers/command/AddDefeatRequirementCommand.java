package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.libs.minecraft.MinecraftMod;
import com.selfdot.libs.minecraft.permissions.Permission;
import com.selfdot.libs.minecraft.permissions.PermissionLevel;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class AddDefeatRequirementCommand extends TrainerCommand {

    public AddDefeatRequirementCommand(MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher) {
        super(mod, dispatcher, "adddefeatrequirement", new Permission(
            "trainers.adddefeatrequirement", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
        ));
    }

    @Override
    protected LiteralArgumentBuilder<ServerCommandSource> node(LiteralArgumentBuilder<ServerCommandSource> root) {
        return super.node(root.then(RequiredArgumentBuilder.<ServerCommandSource, String>
            argument("trainer", string())
            .suggests(new TrainerNameSuggestionProvider())
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("defeatRequirement", string())
                .suggests(new TrainerNameSuggestionProvider())
                .executes(this)
            )
        ));
    }

    @Override
    protected int execute(CommandContext<ServerCommandSource> context, Trainer trainer) {
        String defeatRequirement = StringArgumentType.getString(context, "defeatRequirement");
        trainer.addDefeatRequirement(defeatRequirement);
        context.getSource().sendMessage(Text.literal(
            "Made trainer " + trainer.getName() + " require having defeated " + defeatRequirement
        ));
        return SINGLE_SUCCESS;
    }

}
