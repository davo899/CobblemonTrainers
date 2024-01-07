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
    protected int pageNumber;
    private int maxPerPage = 0;
    private int prevPageSlot = 0;
    private int nextPageSlot = 0;
    private int prevPrevPageSlot = 0;
    private int nextNextPageSlot = 0;

    public PagedScreen(Screen returnsTo, List<T> trackedList, int pageNumber) {
        super(returnsTo);
        this.trackedList = trackedList;
        this.pageNumber = pageNumber;
    }

    public PagedScreen(List<T> trackedList, int pageNumber) { this(null, trackedList, pageNumber); }

    @Override
    public void initialize(Inventory inventory) {
        maxPerPage = (rows - 3) * (columns - 2);
        prevPageSlot = slotIndex(2, 4);
        nextPageSlot = slotIndex(6, 4);
        prevPrevPageSlot = slotIndex(1, 4);
        nextNextPageSlot = slotIndex(7, 4);

        setSlot(inventory, prevPageSlot, Items.ARROW, "Previous Page");
        setSlot(inventory, nextPageSlot, Items.ARROW, "Next Page");
        setSlot(inventory, prevPrevPageSlot, Items.SPECTRAL_ARROW, "Previous Previous Page");
        setSlot(inventory, nextNextPageSlot, Items.SPECTRAL_ARROW, "Next Next Page");

        for (int i = 0; i < Math.min(maxPerPage, trackedList.size() - (maxPerPage * pageNumber)); i++) {
            inventory.setStack(
                (columns + 1) + (columns * (i / (columns - 2))) + (i % (columns - 2)),
                toItem(trackedList.get((maxPerPage * pageNumber) + i))
            );
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if (slotIndex == prevPageSlot) changePage(-1, player);
        else if (slotIndex == nextPageSlot) changePage(1, player);
        else if (slotIndex == prevPrevPageSlot) changePage(-2, player);
        else if (slotIndex == nextNextPageSlot) changePage(2, player);
        else {
            int x = slotIndex % columns;
            int y = slotIndex / columns;

            if (1 <= x && x <= columns - 2 && 1 <= y && y <= rows - 3) {
                int index = (maxPerPage * pageNumber) + (x - 1) + ((y - 1) * (columns - 2));
                if (index < trackedList.size()) onSelected(trackedList.get(index), player);
            }
        }
    }

    private void changePage(int n, PlayerEntity player) {
        int pages = (trackedList.size() / maxPerPage) + 1;
        pageNumber += n;
        while (pageNumber < 0) pageNumber += pages;
        while (pageNumber >= pages) pageNumber -= pages;
        player.openHandledScreen(new TrainerSetupHandlerFactory(this));
    }

    protected abstract ItemStack toItem(T t);

    protected abstract void onSelected(T t, PlayerEntity player);

}
