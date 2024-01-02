package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import com.selfdot.cobblemontrainers.util.CommandUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class RemoveTrainerCommand extends TrainerCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(source -> CommandUtils.hasPermission(source, "selfdot.op.trainers"))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("remove")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string())
                        .suggests(new TrainerNameSuggestionProvider())
                        .executes(this::execute)
                )
            )
        );
    }

    protected int run(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("name", String.class);

        if (!TrainerRegistry.getInstance().removeTrainer(name)) {
            ctx.getSource().sendError(Text.literal("Trainer " + name + " does not exist"));
            return -1;
        }
        ctx.getSource().sendMessage(Text.literal("Removed trainer " + name));
        return 1;
    }

}
