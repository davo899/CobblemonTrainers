package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;

public class TrainerTeamScreen extends Screen {

    private static final int TEAM_MAX_SIZE = 6;
    private final Trainer trainer;

    public TrainerTeamScreen(Trainer trainer) {
        super(new TrainerListScreen());
        this.trainer = trainer;
    }

    @Override
    public void initialize(Inventory inventory) {
        ItemStack trainerItem = new ItemStack(CobblemonItems.POKE_BALL.get());
        trainerItem.setCustomName(Text.literal(trainer.getName()));
        inventory.setStack(columns / 2, trainerItem);

        List<BattlePokemon> team = trainer.getTeam();
        for (int i = 0; i < TEAM_MAX_SIZE; i++) {
            if (i < team.size()) {
                BattlePokemon pokemon = team.get(i);
                inventory.setStack(
                    (2 * columns) + (columns / 2) - 1 + ((i / 3) * columns) + (i % 3),
                    PokemonUtility.pokemonToItem(pokemon.getOriginalPokemon())
                );
            } else {
                ItemStack itemStack = new ItemStack(Items.BEDROCK);
                itemStack.setCustomName(Text.literal("Empty"));
                inventory.setStack((2 * columns) + (columns / 2) - 1 + ((i / 3) * columns) + (i % 3), itemStack);
            }
        }

        if (team.size() < TEAM_MAX_SIZE) {
            ItemStack newPokemonItem = new ItemStack(CobblemonItems.POKE_BALL.get());
            newPokemonItem.setCustomName(Text.literal("New Pokémon"));
            inventory.setStack((2 * columns) + (columns / 2) - 2, newPokemonItem);
        }
    }

    @Override
    public String getDisplayName() {
        return trainer.getName();
    }

}
