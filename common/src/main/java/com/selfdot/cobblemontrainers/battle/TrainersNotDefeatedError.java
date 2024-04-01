package com.selfdot.cobblemontrainers.battle;

import com.cobblemon.mod.common.battles.BattleStartError;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TrainersNotDefeatedError implements BattleStartError {

    private final List<String> notDefeatedTrainers;

    public TrainersNotDefeatedError(List<String> notDefeatedTrainers) {
        this.notDefeatedTrainers = notDefeatedTrainers;
    }

    @NotNull
    @Override
    public MutableText getMessageFor(@NotNull Entity entity) {
        StringBuilder message = new StringBuilder()
            .append("To challenge this trainer, you must have defeated the following trainers: ")
            .append(notDefeatedTrainers.get(0));
        notDefeatedTrainers.stream().skip(1).forEach(trainer -> message.append(", ").append(trainer));
        return Text.literal(Formatting.RED + message.toString());
    }

}
