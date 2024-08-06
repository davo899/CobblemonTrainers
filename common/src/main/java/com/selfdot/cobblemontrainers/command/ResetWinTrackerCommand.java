package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.libs.minecraft.MinecraftMod;
import com.selfdot.libs.minecraft.permissions.Permission;
import com.selfdot.libs.minecraft.permissions.PermissionLevel;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class ResetWinTrackerCommand extends TrainerCommand {

    public ResetWinTrackerCommand(MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher) {
        super(mod, dispatcher, "resetwintracker", new Permission(
            "trainers.resetwintracker", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
        ));
    }

    @Override
    protected LiteralArgumentBuilder<ServerCommandSource> node(LiteralArgumentBuilder<ServerCommandSource> root) {
        return super.node(root
            .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>
                argument("player", EntityArgumentType.player())
                .suggests((context, builder) -> EntityArgumentType.player().listSuggestions(context, builder))
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .executes(this)
                )
            )
        );
    }

    @Override
    protected int execute(CommandContext<ServerCommandSource> context, Trainer trainer) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        CobblemonTrainers.INSTANCE.getTrainerWinTracker().reset(player, trainer);
        player.sendMessage(Text.literal(String.format(
            "Reset win tracker of %s for trainer %s", player.getGameProfile().getName(), trainer.getName())
        ));
        return SINGLE_SUCCESS;
    }
}
