package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class AbilitySelectScreen extends Screen {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private final List<AbilityTemplate> abilities = new ArrayList<>();
    private int baseSlot;
    private final int selectedIndex;

    public AbilitySelectScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerPokemonScreen(trainer, trainerPokemon));
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
        Pokemon pokemon = trainerPokemon.toPokemon();
        pokemon.getForm().getAbilities()
            .forEach(potentialAbility -> abilities.add(potentialAbility.getTemplate()));
        selectedIndex = abilities.indexOf(pokemon.getAbility().getTemplate());
    }

    @Override
    public void initialize(Inventory inventory) {
        setSlot(inventory, columns / 2, CobblemonItems.CLOVER_SWEET, "Abilities");

        baseSlot = (columns * 2) + (columns / 2) - 1;
        for (int i = 0; i < abilities.size(); i++) {
            setSlot(
                inventory,
                baseSlot + i,
                i == selectedIndex ? CobblemonItems.CLOVER_SWEET : CobblemonItems.CHARCOAL,
                Text.translatable(abilities.get(i).getDisplayName()).getString()
            );
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        int diff = slotIndex - baseSlot;
        if (diff != selectedIndex && 0 <= diff && diff < abilities.size()) {
            trainerPokemon.setAbility(abilities.get(diff).create(false));
            trainer.save();
            switchTo(new AbilitySelectScreen(trainer, trainerPokemon));
        }
    }

    @Override
    public String getDisplayName() {
        return "Abilities";
    }
}
