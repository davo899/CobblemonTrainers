package com.selfdot.cobblemontrainers.libs.minecraft.screen;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class Menu<T extends Menu<T>> {

    private final MenuSize size;
    private final Inventory inventory;
    private final Map<String, ViewFactory<T>> viewFactories = new HashMap<>();

    private String view;
    private final List<Component<T>> components = new ArrayList<>();
    private int page = 0;

    @Getter @Setter
    private boolean isBordered = false;

    @Getter @Setter
    private int elementsPerPage;

    protected PlayerEntity player;

    protected abstract void registerViewFactories(Map<String, ViewFactory<T>> viewFactories);

    protected Menu(String title, PlayerEntity player, MenuSize size, String indexView) {
        this.player = player;
        this.size = size;
        this.inventory = new SimpleInventory(9 * rows());
        registerViewFactories(viewFactories);

        navigate(indexView);
        Menu<T> menu = this;
        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.literal(title);
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new MenuHandler(
                    switch (size) {
                        case SIZE_9x3 -> ScreenHandlerType.GENERIC_9X3;
                        case SIZE_9x6 -> ScreenHandlerType.GENERIC_9X6;
                    },
                    syncId, playerInventory, inventory, rows(), menu
                );
            }
        });
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    public int rows() {
        return switch (size) {
            case SIZE_9x3 -> 3;
            case SIZE_9x6 -> 6;
        };
    }

    public int columns() {
        return 9;
    }

    public void onSlotClick(int slot) {
        for (Component<T> component : components) {
            if (slot == component.slot()) {
                component.action().accept(self());
                component.navigation().accept(self());
                break;
            }
        }
    }

    public void navigate(String nextView) {
        view = nextView;
        components.clear();
        for (int i = 0; i < inventory.size(); i++) inventory.setStack(i, new ItemStack(Items.GRAY_STAINED_GLASS_PANE));
        if (isBordered) {
            for (int i = 0; i < columns(); i++) {
                inventory.setStack(i, new ItemStack(Items.BLACK_STAINED_GLASS_PANE));
                inventory.setStack(
                    ((rows() - 1) * columns()) + i, new ItemStack(Items.BLACK_STAINED_GLASS_PANE)
                );
            }
            for (int i = 1; i < rows() - 1; i++) {
                inventory.setStack(columns() * i, new ItemStack(Items.BLACK_STAINED_GLASS_PANE));
                inventory.setStack(
                    (columns() * i) + (columns() - 1), new ItemStack(Items.BLACK_STAINED_GLASS_PANE)
                );
            }
        }

        View<T> view = viewFactories.get(nextView).create(self());
        components.addAll(view.components());
        components.forEach(component -> inventory.setStack(component.slot(), component.icon()));
    }

    public int getPage() {
        return page;
    }

    public void resetPage() {
        page = 0;
    }

    public void refresh() {
        navigate(view);
    }

    public void movePage(int offset, int elementCount) {
        page += offset;
        int pageCount = (elementCount / elementsPerPage) + 1;
        while (page < 0) page += pageCount;
        while (page >= pageCount) page -= pageCount;
        refresh();
    }

}
