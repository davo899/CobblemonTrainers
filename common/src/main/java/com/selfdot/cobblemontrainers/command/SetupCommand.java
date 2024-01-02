package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.screen.TrainerGroupScreen;
import com.selfdot.cobblemontrainers.screen.TrainerListScreen;
import com.selfdot.cobblemontrainers.screen.TrainerSetupHandlerFactory;
import com.selfdot.cobblemontrainers.util.CommandUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class SetupCommand extends TrainerCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(source -> CommandUtils.hasPermission(source, "selfdot.op.trainers"))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setup")
                    .executes(this::execute)
            )
        );
    }

    protected int run(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        if (!source.isExecutedByPlayer() || source.getPlayer() == null) {
            source.sendError(Text.literal("Must be a player to open setup GUI"));
            return -1;
        }

        TrainerSetupHandlerFactory setupHandler = new TrainerSetupHandlerFactory(new TrainerGroupScreen());
        source.getPlayer().openHandledScreen(setupHandler);
        return 1;
    }

}
