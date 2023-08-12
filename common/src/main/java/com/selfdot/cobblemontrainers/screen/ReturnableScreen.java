package com.selfdot.cobblemontrainers.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public abstract class ReturnableScreen extends Screen {

    private final Screen returnTo;

    public ReturnableScreen(Screen returnTo) {
        this.returnTo = returnTo;
    }

    @Override
    public void initialize(Inventory inventory, int rows, int columns) {
        super.initialize(inventory, rows, columns);
        ItemStack itemStack = new ItemStack(Items.BARRIER);
        itemStack.setCustomName(Text.literal("Back"));
        inventory.setStack((rows - 1) * columns, itemStack);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex == (rows - 1) * columns) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(returnTo));
        }
    }

}
