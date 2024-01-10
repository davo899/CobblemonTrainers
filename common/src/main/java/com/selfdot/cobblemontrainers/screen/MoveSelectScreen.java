package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoveSelectScreen extends PagedScreen<Move> {

    private final int moveIndex;
    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private int deleteMoveIndex;

    public MoveSelectScreen(int moveIndex, Trainer trainer, TrainerPokemon trainerPokemon) {
        super(
            new PokemonMovesetScreen(trainer, trainerPokemon),
            Stream.of(
                trainerPokemon.toPokemon().getForm().getMoves().getLevelUpMovesUpTo(trainerPokemon.getLevel()),
                trainerPokemon.toPokemon().getForm().getMoves().getTmMoves(),
                trainerPokemon.toPokemon().getForm().getMoves().getEggMoves(),
                trainerPokemon.toPokemon().getForm().getMoves().getEvolutionMoves(),
                trainerPokemon.toPokemon().getForm().getMoves().getFormChangeMoves(),
                trainerPokemon.toPokemon().getForm().getMoves().getTutorMoves()
            ).flatMap(Collection::stream)
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
    public void initialize(Inventory inventory) {
        deleteMoveIndex = slotIndex(3, 4);

        super.initialize(inventory);

        ItemStack movesItem = new ItemStack(Items.MUSIC_DISC_MELLOHI);
        movesItem.setCustomName(Text.literal("Moves"));
        inventory.setStack(columns / 2, movesItem);

        ItemStack deleteMoveItem = new ItemStack(Items.MUSIC_DISC_CHIRP);
        deleteMoveItem.setCustomName(Text.literal("Delete Move"));
        inventory.setStack(deleteMoveIndex, deleteMoveItem);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if (slotIndex == deleteMoveIndex) {
            trainerPokemon.getMoveset().setMove(moveIndex, null);
            trainer.save();
            player.openHandledScreen(new TrainerSetupHandlerFactory(new PokemonMovesetScreen(trainer, trainerPokemon)));
        }
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
        trainer.save();
        player.openHandledScreen(new TrainerSetupHandlerFactory(new PokemonMovesetScreen(trainer, trainerPokemon)));
    }

    @Override
    public String getDisplayName() {
        return "Moves";
    }

}
