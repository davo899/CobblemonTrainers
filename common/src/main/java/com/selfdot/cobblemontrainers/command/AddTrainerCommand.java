package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.CobblemonPermissions;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class AddTrainerCommand implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(src -> CobblemonTrainersPermissions.checkPermission(
                src, new CobblemonPermission("", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            ))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("add")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string()).executes(this)
                )
            )
        );
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("name", String.class);

        if (!TrainerRegistry.getInstance().addTrainer(new Trainer(name, new ArrayList<>()))) {
            ctx.getSource().sendError(Text.literal("Trainer " + name + " already exists"));
            return -1;
        }
        ctx.getSource().sendMessage(Text.literal("Added new trainer " + name));
        return 1;
    }

}
