package com.selfdot.cobblemontrainers.libs.minecraft.screen;

public interface ViewFactory<T extends Menu<T>> {

    View<T> create(T menu);

}
