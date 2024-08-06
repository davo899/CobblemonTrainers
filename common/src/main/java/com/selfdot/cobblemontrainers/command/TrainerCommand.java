package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.libs.minecraft.MinecraftMod;
import com.selfdot.libs.minecraft.permissions.Permission;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public abstract class TrainerCommand extends CobblemonTrainersCommand {

    public TrainerCommand(
        MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher, String root, Permission permission
    ) {
        super(mod, dispatcher, root, permission);
    }

    protected abstract int execute(
        CommandContext<ServerCommandSource> context, Trainer trainer
    ) throws CommandSyntaxException;

    @Override
    protected int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String trainerName = StringArgumentType.getString(context, "trainer");
        Trainer trainer = CobblemonTrainers.INSTANCE.getTrainerRegistry().getTrainer(trainerName);
        if (trainer == null) {
            context.getSource().sendError(Text.literal("Trainer " + trainerName + " does not exist"));
            return -1;
        }
        return execute(context, trainer);
    }

}
