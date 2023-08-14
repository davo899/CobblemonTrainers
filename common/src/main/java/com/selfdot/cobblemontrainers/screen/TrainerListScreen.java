package com.selfdot.cobblemontrainers.screen;

import com.cobblemon.mod.common.CobblemonItems;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.Iterator;
import java.util.List;

public class TrainerListScreen extends Screen {

    private final List<Trainer> trainerList = TrainerRegistry.getInstance().getAllTrainers().stream().toList();
    private int maxPerPage = 0;

    @Override
    public void initialize(Inventory inventory, int rows, int columns) {
        super.initialize(inventory, rows, columns);
        this.maxPerPage = Math.min((rows - 2) * (columns - 2), trainerList.size());
        int i = 0;
        while (i < maxPerPage) {
            ItemStack itemStack = new ItemStack(CobblemonItems.POKE_BALL.get());
            itemStack.setCustomName(Text.literal(trainerList.get(i).getName()));

            // This formula will fill the non-edge slots of the inventory from a list of items,
            // starting from the top left and moving right:
            // (c + 1) + (c * (i div (c - 2))) + (i % (c - 2))
            inventory.setStack(
                (columns + 1) + (columns * (i / (columns - 2))) + (i % (columns - 2)),
                itemStack
            );
            i++;
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        int x = slotIndex % columns;
        int y = slotIndex / columns;

        // Clicked a trainer
        if (x > 0 && x < columns - 1 && y > 0 && y < rows - 1) {
            int trainerIndex = (x - 1) + ((y - 1) * (columns - 2));
            if (trainerIndex < maxPerPage) {
                player.openHandledScreen(new TrainerSetupHandlerFactory(
                    new TrainerTeamScreen(trainerList.get(trainerIndex))
                ));
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Trainers";
    }

}
