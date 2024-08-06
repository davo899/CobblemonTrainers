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

public class RemoveDefeatRequirementCommand extends TrainerCommand {

    public RemoveDefeatRequirementCommand(MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher) {
        super(mod, dispatcher, "removedefeatrequirement", new Permission(
            "trainers.removedefeatrequirement", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
        ));
    }

    @Override
    protected LiteralArgumentBuilder<ServerCommandSource> node(LiteralArgumentBuilder<ServerCommandSource> root) {
        return super.node(root
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("trainer", string())
                .suggests(new TrainerNameSuggestionProvider())
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("defeatRequirement", string())
                    .suggests((context, builder) -> {
                        Trainer trainer = CobblemonTrainers.INSTANCE.getTrainerRegistry()
                            .getTrainer(StringArgumentType.getString(context, "trainer"));
                        if (trainer == null) return builder.buildFuture();
                        trainer.getDefeatRequiredTrainers().forEach(builder::suggest);
                        return builder.buildFuture();
                    })
                    .executes(this)
                )
            )
        );
    }

    @Override
    protected int execute(CommandContext<ServerCommandSource> context, Trainer trainer) {
        String defeatRequirement = StringArgumentType.getString(context, "defeatRequirement");
        if (!trainer.removeDefeatRequirement(defeatRequirement)) {
            context.getSource().sendError(Text.literal(
                "Trainer " + trainer.getName() + " did not require having defeated " + defeatRequirement
            ));
            return -1;
        }

        context.getSource().sendMessage(Text.literal(
            "Made trainer " + trainer.getName() + " not require having defeated " + defeatRequirement
        ));
        return SINGLE_SUCCESS;
    }

}
