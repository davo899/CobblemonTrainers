package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.List;

public class TrainerTeamScreen extends Screen {

    private static final int TEAM_MAX_SIZE = 6;
    private final Trainer trainer;
    private int newPokemonSlot = 0;

    public TrainerTeamScreen(Trainer trainer) {
        super(new TrainerListScreen(trainer.getGroup()));
        this.trainer = trainer;
    }

    @Override
    public void initialize(Inventory inventory) {
        ItemStack trainerItem = ScreenUtils.withoutAdditional(CobblemonItems.POKE_BALL);
        trainerItem.setCustomName(Text.literal(trainer.getName()));
        inventory.setStack(columns / 2, trainerItem);

        List<BattlePokemon> team = trainer.getBattleTeam();
        for (int i = 0; i < TEAM_MAX_SIZE; i++) {
            if (i < team.size()) {
                BattlePokemon pokemon = team.get(i);
                inventory.setStack(
                    (2 * columns) + (columns / 2) - 1 + ((i / 3) * columns) + (i % 3),
                    PokemonUtility.pokemonToInfoItem(pokemon.getOriginalPokemon())
                );
            } else {
                ItemStack itemStack = ScreenUtils.withoutAdditional(Items.BEDROCK);
                itemStack.setCustomName(Text.literal("Empty"));
                inventory.setStack((2 * columns) + (columns / 2) - 1 + ((i / 3) * columns) + (i % 3), itemStack);
            }
        }

        if (team.size() < TEAM_MAX_SIZE) {
            newPokemonSlot = (2 * columns) + (columns / 2) - 2;
            ItemStack newPokemonItem = ScreenUtils.withoutAdditional(CobblemonItems.POKE_BALL);
            newPokemonItem.setCustomName(Text.literal("New Pokémon"));
            inventory.setStack(newPokemonSlot, newPokemonItem);
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if (slotIndex == newPokemonSlot && trainer.getBattleTeam().size() < TEAM_MAX_SIZE) {
            switchTo(new SpeciesSelectScreen(trainer));
            return;
        }

        int x = slotIndex % columns;
        int y = slotIndex / columns;

        if ((columns / 2) - 1 <= x && x <= (columns / 2) + 1 && 2 <= y && y <= 3) {
            int index = ((y - 2) * 3) + (x - (columns / 2) + 1);
            if (index < trainer.getTeamSize()) switchTo(new TrainerPokemonScreen(trainer, trainer.getTeamSlot(index)));
        }
    }

    @Override
    public String getDisplayName() {
        return trainer.getName();
    }

}
