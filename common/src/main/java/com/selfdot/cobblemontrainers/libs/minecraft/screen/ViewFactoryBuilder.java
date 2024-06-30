package com.selfdot.cobblemontrainers.libs.minecraft.screen;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class ViewFactoryBuilder<T extends Menu<T>> {

    private final List<Supplier<List<Component<T>>>> componentFactories = new ArrayList<>();
    private String returnsTo = "";

    public ViewFactoryBuilder<T> withComponents(Supplier<List<Component<T>>> componentFactory) {
        componentFactories.add(componentFactory);
        return this;
    }

    public ViewFactoryBuilder<T> withComponent(Supplier<Component<T>> componentFactory) {
        return withComponents(() -> List.of(componentFactory.get()));
    }

    public ViewFactoryBuilder<T> withComponent(Component<T> component) {
        return withComponent(() -> component);
    }

    public ViewFactoryBuilder<T> returnsTo(String returnsTo) {
        this.returnsTo = returnsTo;
        return this;
    }

    public ViewFactoryBuilder<T> integerEditor(
        Supplier<Integer> getter,
        Consumer<Integer> setter,
        int min,
        int max,
        int editLow,
        int editHigh,
        Supplier<Item> item,
        Supplier<String> name
    ) {
        return withComponents(() -> List.of(
            new ComponentBuilder<T>(1, 2, Items.RED_DYE)
                .withName("Set to " + min)
                .withAction(menu -> setter.accept(min))
                .refreshes()
                .build(),
            new ComponentBuilder<T>(2, 2, Items.RED_DYE)
                .withName("-" + editHigh)
                .withAction(menu -> setter.accept(Math.max(getter.get() - editHigh, min)))
                .refreshes()
                .build(),
            new ComponentBuilder<T>(3, 2, Items.RED_DYE)
                .withName("-" + editLow)
                .withAction(menu -> setter.accept(Math.max(getter.get() - editLow, min)))
                .refreshes()
                .build(),
            new ComponentBuilder<T>(4, 2, item.get())
                .withName(name.get() + ": " + getter.get())
                .build(),
            new ComponentBuilder<T>(5, 2, Items.GREEN_DYE)
                .withName("+" + editLow)
                .withAction(menu -> setter.accept(Math.min(getter.get() + editLow, max)))
                .refreshes()
                .build(),
            new ComponentBuilder<T>(6, 2, Items.GREEN_DYE)
                .withName("+" + editHigh)
                .withAction(menu -> setter.accept(Math.min(getter.get() + editHigh, max)))
                .refreshes()
                .build(),
            new ComponentBuilder<T>(7, 2, Items.GREEN_DYE)
                .withName("Set to " + max)
                .withAction(menu -> setter.accept(max))
                .refreshes()
                .build()
        ));
    }

    private static final int ELEMENTS_PER_ROW = 7;
    private static final int ELEMENTS_PER_PAGE = ELEMENTS_PER_ROW * 3;
    public <U> ViewFactory<T> paged9x6(
        Supplier<List<U>> elementsSupplier,
        Function<U, ItemStackBuilder> iconFactory,
        BiConsumer<T, U> onElementClick
    ) {
        return menu -> withComponents(() -> {
            List<Component<T>> components = new ArrayList<>();
            List<U> elements = elementsSupplier.get();
            menu.setElementsPerPage(ELEMENTS_PER_PAGE);
            for (
                int i = 0; i < Math.min(ELEMENTS_PER_PAGE, elements.size() - (menu.getPage() * ELEMENTS_PER_PAGE)); i++
            ) {
                U element = elements.get((menu.getPage() * ELEMENTS_PER_PAGE) + i);
                components.add(new ComponentBuilder<T>(
                    (i % ELEMENTS_PER_ROW) + 1, (i / ELEMENTS_PER_ROW) + 1, iconFactory.apply(element)
                ).withAction(menu_ -> onElementClick.accept(menu_, element)).build());
            }

            components.add(new ComponentBuilder<T>(1, 4, Items.SPECTRAL_ARROW)
                .withName("Previous Previous Page")
                .withAction(menu_ -> menu_.movePage(-2, elements.size()))
                .build()
            );
            components.add(new ComponentBuilder<T>(7, 4, Items.SPECTRAL_ARROW)
                .withName("Next Next Page")
                .withAction(menu_ -> menu_.movePage(2, elements.size()))
                .build()
            );
            components.add(new ComponentBuilder<T>(2, 4, Items.ARROW)
                .withName("Previous Page")
                .withAction(menu_ -> menu_.movePage(-1, elements.size()))
                .build()
            );
            components.add(new ComponentBuilder<T>(6, 4, Items.ARROW)
                .withName("Next Page")
                .withAction(menu_ -> menu_.movePage(1, elements.size()))
                .build()
            );
            return components;
        }).build().create(menu);
    }

    public ViewFactory<T> build() {
        return menu -> {
            if (!returnsTo.isEmpty()) {
                withComponent(new ComponentBuilder<T>(
                    menu.columns() / 2, menu.rows() - (menu.isBordered() ? 2 : 1), Items.BARRIER
                ).withName("Back").withAction(Menu::resetPage).navigatesTo(returnsTo).build());
            }
            return new View<>(
                componentFactories.stream().flatMap(componentFactory -> componentFactory.get().stream()).toList()
            );
        };
    }

}
