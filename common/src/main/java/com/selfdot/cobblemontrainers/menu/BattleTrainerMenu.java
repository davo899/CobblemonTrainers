package com.selfdot.cobblemontrainers.menu;

import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.libs.minecraft.screen.*;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

import static com.cobblemon.mod.common.CobblemonItems.POKE_BALL;
import static com.selfdot.cobblemontrainers.libs.minecraft.screen.ItemStackBuilder.itemStack;

public class BattleTrainerMenu extends Menu<BattleTrainerMenu> {

    private static final String GROUPS = "groups";
    private static final String GROUP = "group";

    private String selectedGroup;

    public BattleTrainerMenu(PlayerEntity player) {
        super("Battle Trainer", player, MenuSize.SIZE_9x6, GROUPS);
    }

    @Override
    protected void registerViewFactories(Map<String, ViewFactory<BattleTrainerMenu>> viewFactories) {
        viewFactories.put(GROUPS, new ViewFactoryBuilder<BattleTrainerMenu>()
            .paged9x6(
                () -> CobblemonTrainers.INSTANCE.getTrainerRegistry().getAllTrainers().stream()
                    .map(Trainer::getGroup)
                    .distinct()
                    .toList(),
                group -> itemStack(Items.BOOK).withName(group),
                (menu, group) -> {
                    this.selectedGroup = group;
                    menu.navigate(GROUP);
                }
            )
        );

        viewFactories.put(GROUP, new ViewFactoryBuilder<BattleTrainerMenu>()
            .returnsTo(GROUPS)
            .withComponent(() -> new ComponentBuilder<BattleTrainerMenu>(4, 0, Items.BOOK)
                .withName(selectedGroup)
                .build()
            )
            .paged9x6(
                () -> CobblemonTrainers.INSTANCE.getTrainerRegistry().getAllTrainers().stream()
                    .filter(trainer -> trainer.getGroup().equals(selectedGroup))
                    .toList(),
                trainer -> itemStack(POKE_BALL).withName(trainer.getName()),
                (menu, trainer) -> {
                    player.closeHandledScreen();
                    PokemonUtility.startTrainerBattle((ServerPlayerEntity)player, trainer, null);
                }
            )
        );
    }

}
