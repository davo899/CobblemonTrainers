package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.libs.minecraft.MinecraftMod;
import com.selfdot.libs.minecraft.permissions.Permission;
import com.selfdot.libs.minecraft.permissions.PermissionLevel;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class RenameTrainerCommand extends TrainerCommand {

    public RenameTrainerCommand(MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher) {
        super(mod, dispatcher, "rename", new Permission(
            "trainers.rename", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
        ));
    }

    @Override
    protected LiteralArgumentBuilder<ServerCommandSource> node(LiteralArgumentBuilder<ServerCommandSource> root) {
        return super.node(root
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("trainer", string())
                .suggests(new TrainerNameSuggestionProvider())
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("newName", string())
                    .executes(this)
                )
            )
        );
    }

    @Override
    protected int execute(CommandContext<ServerCommandSource> context, Trainer trainer) {
        String oldName = trainer.getName();
        String newName = StringArgumentType.getString(context, "newName");
        if (CobblemonTrainers.INSTANCE.getTrainerRegistry().getTrainer(newName) != null) {
            context.getSource().sendError(Text.literal("Trainer " + newName + " already exists"));
            return -1;
        }
        CobblemonTrainers.INSTANCE.getTrainerRegistry().removeTrainer(oldName);
        trainer.setName(newName);
        CobblemonTrainers.INSTANCE.getTrainerRegistry().addTrainer(trainer);
        CobblemonTrainers.INSTANCE.getTrainerWinTracker().rename(oldName, newName);
        context.getSource().sendMessage(Text.literal("Renamed trainer " + oldName + " to " + newName));
        return SINGLE_SUCCESS;
    }

}
