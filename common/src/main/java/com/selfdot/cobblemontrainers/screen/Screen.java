package com.selfdot.cobblemontrainers.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;

public interface Screen {

    void initialize(Inventory inventory, int rows, int cols);

    void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player);

    String getDisplayName();

}
