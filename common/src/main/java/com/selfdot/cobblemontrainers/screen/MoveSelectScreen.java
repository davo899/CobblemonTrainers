package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.stream.Collectors;

public class MoveSelectScreen extends PagedScreen<Move> {

    private final int moveIndex;
    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;

    public MoveSelectScreen(int moveIndex, Trainer trainer, TrainerPokemon trainerPokemon) {
        super(
            new PokemonMovesetScreen(trainer, trainerPokemon),
            trainerPokemon.toPokemon().getAllAccessibleMoves().stream()
                .filter(moveTemplate -> trainerPokemon.getMoveset().getMoves().stream().noneMatch(
                    move -> move.getTemplate().equals(moveTemplate)
                ))
                .map(MoveTemplate::create)
                .collect(Collectors.toList()),
            0
        );
        this.moveIndex = moveIndex;
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    protected ItemStack toItem(Move move) {
        ItemStack itemStack = new ItemStack(Items.MUSIC_DISC_MELLOHI);
        itemStack.setCustomName(move.getDisplayName());
        return itemStack;
    }

    @Override
    protected void onSelected(Move move, PlayerEntity player) {
        trainerPokemon.getMoveset().setMove(moveIndex, move);
        player.openHandledScreen(new TrainerSetupHandlerFactory(new PokemonMovesetScreen(trainer, trainerPokemon)));
    }

    @Override
    public String getDisplayName() {
        return "Moves";
    }

}
