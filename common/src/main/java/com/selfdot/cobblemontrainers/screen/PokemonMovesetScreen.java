package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveSet;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.List;

public class PokemonMovesetScreen extends Screen {

    private final Trainer trainer;
    private final TrainerPokemon trainerPokemon;
    private int[] moveSlots = {0, 0, 0, 0};

    public PokemonMovesetScreen(Trainer trainer, TrainerPokemon trainerPokemon) {
        super(new TrainerPokemonScreen(trainer, trainerPokemon));
        this.trainer = trainer;
        this.trainerPokemon = trainerPokemon;
    }

    @Override
    public void initialize(Inventory inventory) {
        ItemStack movesItem = new ItemStack(Items.MUSIC_DISC_5);
        movesItem.setCustomName(Text.literal("Moveset"));
        inventory.setStack(columns / 2, movesItem);

        moveSlots[0] = (2 * columns) + 2;
        moveSlots[1] = moveSlots[0] + 1;
        moveSlots[2] = moveSlots[0] + 3;
        moveSlots[3] = moveSlots[0] + 4;

        for (int i = 0; i < 4; i++) {
            ItemStack moveItem = new ItemStack(Items.BARRIER);
            moveItem.setCustomName(Text.literal("Empty"));
            inventory.setStack(moveSlots[i], moveItem);
        }
        List<Move> moves = trainerPokemon.getMoveset().getMoves();
        for (int i = 0; i < moves.size(); i++) {
            ItemStack moveItem = new ItemStack(Items.MUSIC_DISC_5);
            moveItem.setCustomName(moves.get(i).getDisplayName());
            inventory.setStack(moveSlots[i], moveItem);
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        for (int i = 0; i < 4; i++) {
            if (slotIndex == moveSlots[i]) {
                player.openHandledScreen(new TrainerSetupHandlerFactory(
                    new MoveSelectScreen(i, trainer, trainerPokemon)
                ));
                return;
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Moveset";
    }

}
