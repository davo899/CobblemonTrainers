package com.selfdot.cobblemontrainers.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.List;

public abstract class PagedScreen<T> extends Screen {

    private final List<T> trackedList;
    private final int pageNumber;
    private int maxPerPage = 0;
    private int prevPageSlot = 0;
    private int nextPageSlot = 0;

    public PagedScreen(List<T> trackedList, int pageNumber) {
        this.trackedList = trackedList;
        this.pageNumber = pageNumber;
    }

    @Override
    public void initialize(Inventory inventory) {
        maxPerPage = (rows - 3) * (columns - 2);
        prevPageSlot = ((rows - 2) * columns) + 1;
        nextPageSlot = ((rows - 2) * columns) + (columns - 2);

        ItemStack prevPageItem = new ItemStack(Items.SPECTRAL_ARROW);
        prevPageItem.setCustomName(Text.literal("Previous Page"));
        inventory.setStack(prevPageSlot, prevPageItem);

        ItemStack nextPageItem = new ItemStack(Items.SPECTRAL_ARROW);
        nextPageItem.setCustomName(Text.literal("Next Page"));
        inventory.setStack(nextPageSlot, nextPageItem);

        for (int i = 0; i < Math.min(maxPerPage * (pageNumber + 1), trackedList.size()); i++) {
            inventory.setStack(
                (columns + 1) + (columns * (i / (columns - 2))) + (i % (columns - 2)),
                toItem(trackedList.get((maxPerPage * pageNumber) + i))
            );
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        int x = slotIndex % columns;
        int y = slotIndex / columns;

        if (1 <= x && x <= columns - 2 && 1 <= y && y <= rows - 3) {
            int index = (x - 1) + ((y - 1) * (columns - 2));
            if (index < trackedList.size()) onSelected(trackedList.get(index), player);
        }
    }

    protected abstract ItemStack toItem(T t);

    protected abstract void onSelected(T t, PlayerEntity player);

}
