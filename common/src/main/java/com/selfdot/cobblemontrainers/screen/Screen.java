package com.selfdot.cobblemontrainers.screen;

import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public abstract class Screen {

    protected int rows = 0;
    protected int columns = 0;
    private Screen returnsTo;
    private int backButtonSlot = 0;

    public Screen() { }
    public Screen(Screen returnsTo) {
        this.returnsTo = returnsTo;
    }

    public void initialize(Inventory inventory, int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        ScreenUtils.fill(inventory, Items.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < columns; i++) {
            inventory.setStack(i, new ItemStack(Items.BLACK_STAINED_GLASS_PANE));
            inventory.setStack(((rows - 1) * columns) + i, new ItemStack(Items.BLACK_STAINED_GLASS_PANE));
        }
        for (int i = 1; i < rows - 1; i++) {
            inventory.setStack(columns * i, new ItemStack(Items.BLACK_STAINED_GLASS_PANE));
            inventory.setStack((columns * i) + (columns - 1), new ItemStack(Items.BLACK_STAINED_GLASS_PANE));
        }
        if (returnsTo != null) {
            backButtonSlot = ((rows - 2) * columns) + (columns / 2);
            ItemStack itemStack = new ItemStack(Items.BARRIER);
            itemStack.setCustomName(Text.literal("Back"));
            inventory.setStack(backButtonSlot, itemStack);
        }
    }

    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (returnsTo != null && slotIndex == backButtonSlot) {
            player.openHandledScreen(new TrainerSetupHandlerFactory(returnsTo));
        }
    };

    public abstract String getDisplayName();

}
