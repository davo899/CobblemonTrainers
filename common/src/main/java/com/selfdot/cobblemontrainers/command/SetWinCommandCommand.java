package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import com.selfdot.cobblemontrainers.util.CommandUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class SetWinCommandCommand extends TrainerCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(source -> CommandUtils.hasPermission(source, "selfdot.op.trainers"))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setwincommand")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("winCommand", string())
                        .executes(this::execute)
                    )
                )
            )
        );
    }

    protected int run(CommandContext<ServerCommandSource> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        String winCommand = StringArgumentType.getString(ctx, "winCommand");

        Trainer trainer = TrainerRegistry.getInstance().getTrainer(name);

        if (trainer == null) {
            ctx.getSource().sendError(Text.literal("Trainer " + name + " does not exist"));
            return -1;
        }

        trainer.setWinCommand(winCommand);
        ctx.getSource().sendMessage(Text.literal(
            "Set win command for trainer " + name + " to '" + winCommand + "'"
        ));
        return 1;
    }

}
