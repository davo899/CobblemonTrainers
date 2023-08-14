package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class RenameTrainerCommand implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(src -> CobblemonTrainersPermissions.checkPermission(
                src, new CobblemonPermission("", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            ))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("rename")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("oldName", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("newName", string()).executes(this)
                    )
                )
            )
        );
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) {
        String oldName = ctx.getArgument("oldName", String.class);
        String newName = ctx.getArgument("newName", String.class);

        Trainer trainer = TrainerRegistry.getInstance().getTrainer(oldName);
        if (trainer == null) {
            ctx.getSource().sendError(Text.literal("Trainer " + oldName + " does not exist"));
            return -1;
        }

        if (TrainerRegistry.getInstance().getTrainer(newName) != null) {
            ctx.getSource().sendError(Text.literal("Trainer " + newName + " already exists"));
            return -1;
        }

        trainer.setName(newName);
        TrainerRegistry.getInstance().removeTrainer(oldName);
        TrainerRegistry.getInstance().addTrainer(trainer);
        ctx.getSource().sendMessage(Text.literal("Renamed trainer " + oldName + " to " + newName));
        return 1;
    }

}
