package com.selfdot.cobblemontrainers.libs.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import java.util.function.Consumer;

import static com.selfdot.cobblemontrainers.libs.minecraft.screen.ItemStackBuilder.itemStack;
import static com.selfdot.cobblemontrainers.libs.minecraft.screen.ItemStackBuilder.skullOf;

public class ComponentBuilder<T extends Menu<T>> {

    private final int x;
    private final int y;
    private final ItemStackBuilder icon;
    private Consumer<T> action = menu -> {};
    private Consumer<T> navigation = menu -> {};

    public ComponentBuilder(int x, int y, ItemStackBuilder icon) {
        this.x = x;
        this.y = y;
        this.icon = icon;
    }

    public ComponentBuilder(int x, int y, Item item) {
        this(x, y, itemStack(item));
    }

    public ComponentBuilder(int x, int y, PlayerEntity player) {
        this(x, y, skullOf(player));
    }

    public ComponentBuilder<T> withName(String name) {
        icon.withName(name);
        return this;
    }

    public ComponentBuilder<T> withAdditional() {
        icon.withAdditional();
        return this;
    }

    public ComponentBuilder<T> withLore(String loreLine) {
        icon.withLore(loreLine);
        return this;
    }

    public ComponentBuilder<T> withAction(Consumer<T> action) {
        this.action = action;
        return this;
    }

    public ComponentBuilder<T> navigatesTo(String view) {
        this.navigation = menu -> menu.navigate(view);
        return this;
    }

    public ComponentBuilder<T> refreshes() {
        this.navigation = Menu::refresh;
        return this;
    }

    public Component<T> build() {
        return new Component<>((y * 9) + x, icon.build(), action, navigation);
    }

}
