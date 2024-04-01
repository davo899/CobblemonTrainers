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
        return Text.literal(Formatting.RED + String.format("You must defeat following trainers " +
                        "to challenge this trainer: %s", this.notDefeatedTrainers));
    }
}
