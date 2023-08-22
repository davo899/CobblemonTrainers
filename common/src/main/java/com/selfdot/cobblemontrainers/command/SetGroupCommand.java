package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class SetGroupCommand extends TrainerCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(src -> CobblemonTrainersPermissions.checkPermission(
                src, new CobblemonPermission("", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            ))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setgroup")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("group", string())
                        .suggests(new TrainerGroupSuggestionProvider())
                        .executes(this::execute)
                    )
                )
            )
        );
    }

    protected int run(CommandContext<ServerCommandSource> ctx) {
        String trainerName = ctx.getArgument("trainer", String.class);
        String group = ctx.getArgument("group", String.class);

        Trainer trainer = TrainerRegistry.getInstance().getTrainer(trainerName);

        if (trainer == null) {
            ctx.getSource().sendError(Text.literal("Trainer " + trainerName + " does not exist"));
            return -1;
        }

        trainer.setGroup(group);
        ctx.getSource().sendMessage(Text.literal("Set group for trainer " + trainerName + " to " + group));
        return 1;
    }

}
