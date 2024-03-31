package com.selfdot.cobblemontrainers.battle;

import com.cobblemon.mod.common.battles.BattleStartError;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

public class TrainerMaximumLevelError implements BattleStartError {

    private final int maxLevel;

    public TrainerMaximumLevelError(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    @NotNull
    @Override
    public MutableText getMessageFor(@NotNull Entity entity) {
        return Text.literal(Formatting.RED + "Your Pokémon must be no higher than level " + maxLevel + " to battle this trainer.");
    }

}
