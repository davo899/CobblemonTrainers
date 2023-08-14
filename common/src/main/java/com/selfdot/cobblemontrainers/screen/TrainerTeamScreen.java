package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

public class TrainerTeamScreen extends Screen {

    private final Trainer trainer;

    public TrainerTeamScreen(Trainer trainer) {
        super(new TrainerListScreen());
        this.trainer = trainer;
    }

    @Override
    public void initialize(Inventory inventory) {
        if (columns - 2 < 6 || rows - 2 < 1) return;

        List<BattlePokemon> team = trainer.getTeam();
        for (int i = 0; i < 6 && i < team.size(); i++) {
            BattlePokemon pokemon = team.get(i);
            ItemStack item = PokemonUtility.pokemonToItem(pokemon.getOriginalPokemon());
            NbtCompound slotNbt = item.getOrCreateSubNbt("slot");
            slotNbt.putInt("slot", i);
            item.setSubNbt("slot", slotNbt);
            inventory.setStack(columns + 1 + i, item);
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
