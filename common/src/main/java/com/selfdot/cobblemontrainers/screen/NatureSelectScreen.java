package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.pokemon.Nature;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NatureSelectScreen extends PagedScreen<Nature> {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;

    public NatureSelectScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(
            new TrainerPokemonScreen(trainer, trainerPokemon),
            Natures.INSTANCE.all().stream().toList(),
            0
        );
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    protected ItemStack toItem(Nature nature) {
        ItemStack itemStack = new ItemStack(Items.CYAN_DYE);
        itemStack.setCustomName(Text.translatable(nature.getDisplayName()));
        Stat increasedStat = nature.getIncreasedStat();
        Stat decreasedStat = nature.getDecreasedStat();
        if (increasedStat == null || decreasedStat == null) {
            ScreenUtils.addLore(itemStack, new Text[]{
                Text.literal(Formatting.GRAY + "No stat change")
            });
        } else {
            ScreenUtils.addLore(itemStack, new Text[]{
                Text.literal(Formatting.GREEN + "+" + increasedStat.getDisplayName().getString()),
                Text.literal(Formatting.RED + "-" + decreasedStat.getDisplayName().getString())
            });
        }
        return itemStack;
    }

    @Override
    protected void onSelected(Nature nature, PlayerEntity player) {
        trainerPokemon.setNature(nature);
        player.openHandledScreen(new TrainerSetupHandlerFactory(new TrainerPokemonScreen(trainer, trainerPokemon)));
    }

    @Override
    public String getDisplayName() {
        return "Nature";
    }

}
