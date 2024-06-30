package com.selfdot.cobblemontrainers.libs.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class MenuHandler extends GenericContainerScreenHandler {

    private final Menu<?> menu;

    public MenuHandler(
        ScreenHandlerType<?> type,
        int syncId,
        PlayerInventory playerInventory,
        Inventory inventory,
        int rows,
        Menu<?> menu
    ) {
        super(type, syncId, playerInventory, inventory, rows);
        this.menu = menu;
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return false;
    }

    @Override
    protected void dropInventory(PlayerEntity player, Inventory inventory) { }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        menu.onSlotClick(slotIndex);
    }

}
