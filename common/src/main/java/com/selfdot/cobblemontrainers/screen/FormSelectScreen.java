package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class FormSelectScreen extends PagedScreen<FormData> {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;

    public FormSelectScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerPokemonScreen(trainer, trainerPokemon), trainerPokemon.getSpecies().getForms(), 0);
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    protected ItemStack toItem(FormData formData) {
        ItemStack itemStack = new ItemStack(CobblemonItems.ULTRA_BALL.get());
        itemStack.setCustomName(Text.literal(formData.getName()));
        return itemStack;
    }

    @Override
    protected void onSelected(FormData formData, PlayerEntity player) {
        trainerPokemon.setFormData(formData);
        player.openHandledScreen(new TrainerSetupHandlerFactory(new TrainerPokemonScreen(trainer, trainerPokemon)));
    }

    @Override
    public String getDisplayName() {
        return "Forms";
    }

}
