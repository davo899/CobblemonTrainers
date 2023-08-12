package com.selfdot.cobblemontrainers.screen;

import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.Iterator;

public class TrainerListScreen implements Screen {

    @Override
    public void initialize(Inventory inventory, int rows, int columns) {
        ScreenUtils.fill(inventory, Items.GLASS_PANE);
        Iterator<String> trainerNamesIterator = TrainerRegistry.getInstance().getAllTrainerNames().iterator();
        int maxPerPage = (rows - 2) * (columns - 2);
        int i = 0;
        while (i < maxPerPage && trainerNamesIterator.hasNext()) {
            String name = trainerNamesIterator.next();
            ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
            itemStack.setCustomName(Text.literal(name));

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

    }

    @Override
    public String getDisplayName() {
        return "Trainers";
    }

}
