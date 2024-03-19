package com.selfdot.cobblemontrainers.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import static com.selfdot.cobblemontrainers.screen.TrainerSetupScreenHandler.COLUMNS;
import static com.selfdot.cobblemontrainers.screen.TrainerSetupScreenHandler.ROWS;

public class TrainerSetupHandlerFactory implements NamedScreenHandlerFactory {

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
        return new TrainerSetupScreenHandler(screen, ScreenHandlerType.GENERIC_9X6, syncId, inv, inventory, rows());
    }

}
