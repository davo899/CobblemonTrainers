package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class EVSelectScreen extends Screen {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;

    public EVSelectScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerPokemonScreen(trainer, trainerPokemon));
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        ItemStack itemStack = ScreenUtils.withoutAdditional(CobblemonItems.FLOWER_SWEET);
        itemStack.setCustomName(Text.literal("EVs"));
        inventory.setStack(columns / 2, itemStack);

        for (int i = 0; i < 3; i++) {
            Stats stat = ScreenUtils.STATS[i];
            itemStack = ScreenUtils.statVitaminItem(stat);
            itemStack.setCustomName(stat.getDisplayName());
            inventory.setStack((columns * 2) + (columns / 2) - 1 + i, itemStack);
        }
        for (int i = 0; i < 3; i++) {
            Stats stat = ScreenUtils.STATS[3 + i];
            itemStack = ScreenUtils.statVitaminItem(stat);
            itemStack.setCustomName(stat.getDisplayName());
            inventory.setStack((columns * 3) + (columns / 2) - 1 + i, itemStack);
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        switch (slotIndex - ((columns * 2) + (columns / 2) - 1)) {
            case 0 -> switchTo(new EVEditScreen(Stats.HP, trainer, trainerPokemon));
            case 1 -> switchTo(new EVEditScreen(Stats.ATTACK, trainer, trainerPokemon));
            case 2 -> switchTo(new EVEditScreen(Stats.DEFENCE, trainer, trainerPokemon));
        }

        switch (slotIndex - ((columns * 3) + (columns / 2) - 1)) {
            case 0 -> switchTo(new EVEditScreen(Stats.SPECIAL_ATTACK, trainer, trainerPokemon));
            case 1 -> switchTo(new EVEditScreen(Stats.SPECIAL_DEFENCE, trainer, trainerPokemon));
            case 2 -> switchTo(new EVEditScreen(Stats.SPEED, trainer, trainerPokemon));
        }
    }

    @Override
    public String getDisplayName() {
        return "EVs";
    }
}
