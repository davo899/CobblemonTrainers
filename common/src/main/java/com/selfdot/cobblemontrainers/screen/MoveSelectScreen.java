package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokemon.moves.Learnset;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoveSelectScreen extends PagedScreen<Move> {

    private final int moveIndex;
    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private int deleteMoveIndex;

    private static List<Move> moveSelection(TrainerPokemon trainerPokemon) {
        Learnset learnset = trainerPokemon.toPokemon().getForm().getMoves();
        return Stream.of(
            learnset.getLevelUpMovesUpTo(trainerPokemon.getLevel()),
            learnset.getTmMoves(),
            learnset.getEggMoves(),
            learnset.getEvolutionMoves(),
            learnset.getFormChangeMoves(),
            learnset.getTutorMoves()
        )
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(
                (moveTemplate) -> moveTemplate.getName() + moveTemplate.getElementalType().getName(),
                p -> p, (p, q) -> p
            )).values().stream()
            .filter(moveTemplate -> trainerPokemon.getMoveset().getMoves().stream().noneMatch(
                move -> move.getTemplate().equals(moveTemplate)
            ))
            .map(MoveTemplate::create)
            .sorted(Comparator.comparing(Move::getName))
            .toList();
    }

    public MoveSelectScreen(int moveIndex, Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new PokemonMovesetScreen(trainer, trainerPokemon), moveSelection(trainerPokemon), 0);
        this.moveIndex = moveIndex;
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        deleteMoveIndex = slotIndex(3, 4);

        super.initialize(inventory);

        ItemStack movesItem = ScreenUtils.withoutAdditional(Items.MUSIC_DISC_MELLOHI);
        movesItem.setCustomName(Text.literal("Moves"));
        inventory.setStack(columns / 2, movesItem);

        ItemStack deleteMoveItem = ScreenUtils.withoutAdditional(Items.MUSIC_DISC_CHIRP);
        deleteMoveItem.setCustomName(Text.literal("Delete Move"));
        inventory.setStack(deleteMoveIndex, deleteMoveItem);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if (slotIndex == deleteMoveIndex) {
            trainerPokemon.getMoveset().setMove(moveIndex, null);
            trainer.save();
            switchTo(new PokemonMovesetScreen(trainer, trainerPokemon));
        }
    }

    @Override
    protected ItemStack toItem(Move move) {
        return ScreenUtils.moveItem(move);
    }

    @Override
    protected void onSelected(Move move, PlayerEntity player) {
        trainerPokemon.getMoveset().setMove(moveIndex, move);
        trainer.save();
        switchTo(new PokemonMovesetScreen(trainer, trainerPokemon));
    }

    @Override
    public String getDisplayName() {
        return "Moves";
    }

}
