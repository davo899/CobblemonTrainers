package com.selfdot.cobblemontrainers.screen;

public interface ViewFactory<T extends Menu<T>> {

    View<T> create(T menu);

}
