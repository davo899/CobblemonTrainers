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

import java.util.ArrayList;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class SetMoneyRewardCommand extends TrainerCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(src -> CobblemonTrainersPermissions.checkPermission(
                src, new CobblemonPermission("", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            ))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setmoneyreward")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, Integer>
                        argument("amount", integer())
                        .executes(this::execute)
                    )
                )
            )
        );
    }

    protected int run(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("name", String.class);
        Integer moneyReward = ctx.getArgument("amount", Integer.class);

        Trainer trainer = TrainerRegistry.getInstance().getTrainer(name);

        if (trainer == null) {
            ctx.getSource().sendError(Text.literal("Trainer " + name + " does not exist"));
            return -1;
        }
        if (moneyReward < 0) {
            ctx.getSource().sendError(Text.literal("Money reward cannot be negative"));
            return -1;
        }

        trainer.setMoneyReward(moneyReward);
        ctx.getSource().sendMessage(Text.literal("Set money reward for trainer " + name + " to " + moneyReward));
        return 1;
    }

}
