package com.selfdot.cobblemontrainers.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class TrainerSetupScreenHandler extends GenericContainerScreenHandler {

    public static final int ROWS = 6;
    public static final int COLUMNS = 9;

    private static final long SCREEN_SWITCH_MINIMUM_TIME_MILLIS = 150;

    private Screen screen;
    private long lastSwitchedScreens = -1;

    public TrainerSetupScreenHandler(
        Screen screen,
        ScreenHandlerType<?> type,
        int syncId,
        PlayerInventory playerInventory,
        Inventory inventory,
        int rows
    ) {
        super(type, syncId, playerInventory, inventory, rows);
        this.screen = screen;
        screen.setHandler(this);
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return false;
    }

    @Override
    protected void dropInventory(PlayerEntity player, Inventory inventory) { }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        screen.onSlotClick(slotIndex, button, actionType, player);
    }

    public void switchTo(Screen newScreen) {
        if (System.currentTimeMillis() - lastSwitchedScreens < SCREEN_SWITCH_MINIMUM_TIME_MILLIS) return;
        newScreen.initialize(getInventory(), ROWS, COLUMNS);
        newScreen.setHandler(this);
        this.screen = newScreen;
        lastSwitchedScreens = System.currentTimeMillis();
    }

}
