package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.command.argument.PartySlotArgumentType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
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

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class AddFromPartyCommand extends TrainerCommand {

    public AddFromPartyCommand(MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher) {
        super(mod, dispatcher, "addfromparty", new Permission(
            "trainers.addfromparty", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
        ));
    }

    @Override
    protected LiteralArgumentBuilder<ServerCommandSource> node(LiteralArgumentBuilder<ServerCommandSource> root) {
        return super.node(root
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("trainer", string())
                .suggests(new TrainerNameSuggestionProvider())
                .then(RequiredArgumentBuilder.<ServerCommandSource, Integer>
                    argument("pokemon", PartySlotArgumentType.Companion.partySlot())
                    .executes(this)
                )
            )
        );
    }

    @Override
    protected int execute(CommandContext<ServerCommandSource> context, Trainer trainer) {
        if (trainer.getTeamSize() >= 6) {
            context.getSource().sendError(Text.literal("Trainer's team is full"));
            return -1;
        }
        Pokemon pokemon = PartySlotArgumentType.Companion.getPokemon(context, "pokemon");
        trainer.addPokemon(pokemon);
        context.getSource().sendMessage(Text.literal(
            "Added " + pokemon.getDisplayName().getString() + " to " + trainer.getName() + "'s party"
        ));
        return SINGLE_SUCCESS;
    }

}
