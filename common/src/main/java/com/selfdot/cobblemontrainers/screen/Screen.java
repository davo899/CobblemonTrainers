package com.selfdot.cobblemontrainers.screen;

import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public abstract class Screen {

    protected int rows = 0;
    protected int columns = 0;

    public void initialize(Inventory inventory, int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        ScreenUtils.fill(inventory, Items.GLASS_PANE);
    }

    public abstract void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player);

    public abstract String getDisplayName();

}
