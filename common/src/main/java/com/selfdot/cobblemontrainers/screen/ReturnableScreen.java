package com.selfdot.cobblemontrainers.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public abstract class ReturnableScreen extends Screen {

    private final Screen returnTo;
    private int backButtonSlot = 0;

    public ReturnableScreen(Screen returnTo) {
        this.returnTo = returnTo;
    }

    @Override
    public void initialize(Inventory inventory, int rows, int columns) {
        super.initialize(inventory, rows, columns);
        backButtonSlot = ((rows - 2) * columns) + (columns / 2);
        ItemStack itemStack = new ItemStack(Items.BARRIER);
        itemStack.setCustomName(Text.literal("Back"));
        inventory.setStack(backButtonSlot, itemStack);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex == backButtonSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(returnTo));
        }
    }

}
