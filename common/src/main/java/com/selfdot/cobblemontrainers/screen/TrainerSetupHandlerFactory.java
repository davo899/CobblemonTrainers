package com.selfdot.cobblemontrainers.screen;

import com.selfdot.cobblemontrainers.util.ScreenUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class TrainerSetupHandlerFactory implements NamedScreenHandlerFactory {

    private static final int ROWS = 6;
    private static final int COLUMNS = 9;

    private final Screen screen;

    public TrainerSetupHandlerFactory(Screen screen) {
        this.screen = screen;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal(screen.getDisplayName());
    }

    public int rows() {
        return ROWS;
    }

    public int size() {
        return rows() * COLUMNS;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        SimpleInventory inventory = new SimpleInventory(size());
        screen.initialize(inventory, ROWS, COLUMNS);
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, inventory, rows()) {
            @Override
            public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
                screen.onSlotClick(slotIndex, button, actionType, player);
            }
        };
    }

}
