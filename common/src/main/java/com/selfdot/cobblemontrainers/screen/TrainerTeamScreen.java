package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.List;

public class TrainerTeamScreen extends Screen {

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
        for (int i = 0; i < Math.min(team.size(), 6); i++) {
            BattlePokemon pokemon = team.get(i);
            inventory.setStack(
                (2 * columns) + (columns / 2) - 1 + ((i / 3) * columns) + (i % 3),
                PokemonUtility.pokemonToItem(pokemon.getOriginalPokemon())
            );
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public String getDisplayName() {
        return trainer.getName();
    }

}
