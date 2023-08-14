package com.selfdot.cobblemontrainers.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

    protected abstract ItemStack toItem(T t);

}
